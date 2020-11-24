#!/usr/bin/perl -w
use strict;
use YAML qw(LoadFile DumpFile Dump);
use JSON;
use Storable;
use URL::Encode qw(url_encode_utf8 url_decode_utf8);
use Clone qw(clone);
use List::Util 'shuffle';


my $maximum_number_of_input_lines = 100000000;
my $tau = 10;
my $whichabstractions = "allabs"; #"concretePO"; # allabs or concretePO

my $CFG = {
        support_measure                                 => undef,
        tau                                             => undef,
        minimum_pattern_size                            => 1,
        maximum_pattern_size                            => 2,
        maximum_number_of_input_lines                   => undef, # -1 = all

        evaluation_strategy                             => "strategy-6",
        maximum_number_of_independent_sets              => undef,

        print_frequent_triple_patterns                  => 1,
        print_frequent_triple_patterns_with_mappings    => 0,
        print_frequent_patterns                         => 0,
        print_frequent_patterns_with_mappings           => 0,
        print_total_number_of_mappings                  => 0,

        store_frequent_triple_patterns                  => 0,
        store_frequent_triple_patterns_with_mappings    => 0,
        store_frequent_patterns                         => 1,
        store_frequent_patterns_with_mappings           => 1,

        shorten_terms                                   => 1,

        input_file                                      => undef, # will be set later
        output_file                                     => undef, # will be set later
        incremental_output_file                         => undef, # will be set later

        allowed_join_types => {
                "s-s" => 1,
                "s-p" => 1,
                "s-o" => 1,
                "p-s" => 1,
                "p-p" => 1,
                "p-o" => 1,
                "o-s" => 1,
                "o-p" => 1,
                "o-o" => 1,
        },

        ignore_patterns => {
		#"property"			=> 1,
                "label"                         => 1,
                "equivalentClass"               => 1,
                "wasDerivedFrom"                => 1,
                "comment"                       => 1,
                "wikidata"                      => 1,
                "Thing"                         => 1,
                "ontologydesignpatterns"        => 1,
        },

        verbose                                 => 1,

        measurements => {
                time                            => 1,
                growth                          => 1,
                total_number_of_mappings        => 1,
                incompatible_mappings           => 1,
                compatible_mappings             => 1,
        },


        prefix_definitions => {
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#"                   => "rdf",
                "http://www.w3.org/2000/01/rdf-schema#"                         => "rdfs",
                "http://dbpedia.org/resource/"                                  => "dbr",
                "http://dbpedia.org/ontology/"                                  => "dbo",
                "http://dbpedia.org/property/"                                  => "dbp",
                "http://purl.org/dc/elements/1.1/"                              => "dc",
                "http://xmlns.com/foaf/0.1/"                                    => "foaf",
                "http://yago-knowledge.org/resource/"                           => "yago",
                "http://www.w3.org/2004/02/skos/core#"                          => "skos",
                "http://schema.org/"                                            => "schema",
                "http://www.wikidata.org/prop/direct-normalized/"               => "wdtn",
                "http://www.wikidata.org/entity/"                               => "wd",
                "http://www.wikidata.org/entity/statement/"                     => "wds",
                "http://www.wikidata.org/value/"                                => "wdv",
                "http://www.wikidata.org/prop/direct/"                          => "wdt",
                "http://wikiba.se/ontology#"                                    => "wikibase",
                "http://www.wikidata.org/prop/"                                 => "wp",
                "http://www.wikidata.org/prop/statement/"                       => "wps",
                "http://www.wikidata.org/prop/qualifier/"                       => "wpq",
                "http://www.w3.org/2002/07/owl#"                                => "owl",
                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#"        => "odp",
                "http://www.w3.org/2001/XMLSchema#"                             => "xsd",
                "http://www.w3.org/ns/prov#"                                    => "prov",
                "http://psink.de/scio/"                                         => "scio",
                "http://scio/data/"                                             => "sciodata",
                "http://www.example.org/"                                       => "ex",
        },
        number_of_workers 			=> 1,
        batch_size 				=> 1,
        measure_pattern_has_been_seen_before 	=> 1,
        measure_growth 				=> 1,
        measure_time_in_evaluation 		=> 1,
        runtime_output 				=> undef, # will be set later
        context 				=> "massivecorrelation",
        linguistic_pattern                      => undef, # will be set later
        set_of_entities                         => [], # will be populated later
};



my $entityfile = "/opt/massive_correlation/data/dbpedia-2015-04/politicians.txt";
#my $ntfile = "/opt/massive_correlation/data/dbpedia-2015-04/DI1I4M.nt";
my $ntfile = "/opt/massive_correlation/code/democratic/politicians_1hop.nt";
my $CFG_file = "lamgrapami-$maximum_number_of_input_lines-$tau-$whichabstractions.yml";

my $entities = [];
open(DAT,"<$entityfile");
while(defined(my $line=<DAT>)){
	$line =~ s/\n//;
	push(@{$entities}, $line);
}
close DAT;

my $cnt_entities = scalar @{$entities};

my $this_CFG = clone($CFG);
$this_CFG->{set_of_entities} = $entities;

if($whichabstractions eq "allabs"){
	$this_CFG->{allowed_abstractions} = {
                s => 1,
                p => 1,
                o => 1,
                sp => 1,
                so => 1,
                po => 1,
                spo => 0,
                xxx => 0,
                xxy => 0,
                xyx => 0,
                xyy => 0,
                xyz => 0,
        };
}
elsif($whichabstractions eq "concretePO"){
        $this_CFG->{allowed_abstractions} = {
                s => 1,
                p => 0,
                o => 0,
                sp => 0,
                so => 0,
                po => 0,
                spo => 0,
                xxx => 0,
                xxy => 0,
                xyx => 0,
                xyy => 0,
                xyz => 0,
        };
} else {
	die "unexpected value for whichabstractions\n";
}

{
	$this_CFG->{maximum_number_of_input_lines} = $maximum_number_of_input_lines; 
	$this_CFG->{maximum_number_of_independent_sets} = 10000000;
	$this_CFG->{support_measure} = "MIS_e_emb";
	$this_CFG->{tau} = $tau;

	$this_CFG->{input_file} = $ntfile;	
	$this_CFG->{incremental_output_file} = "incremental_results-$maximum_number_of_input_lines-$tau-$whichabstractions.txt";
	$this_CFG->{output_file} = "results-$maximum_number_of_input_lines-$tau-$whichabstractions.yml";
	$this_CFG->{runtime_output} = "runtime_output-$maximum_number_of_input_lines-$tau-$whichabstractions.yml";
	$this_CFG->{linguistic_pattern} = "democratic_politician";
	$this_CFG->{complex_measures} = 1;
	$this_CFG->{shuffle_C_last} = 1,
	$this_CFG->{more_abstract_optimization} = 1;
	$this_CFG->{seen_patterns_shared} = 1;

	#print Dump { CFG => $this_CFG }; <STDIN>;

	DumpFile($CFG_file, $this_CFG);

	print " > $CFG_file\n";
	my $CMD = "perl /opt/massive_correlation/code/EnglishDBpediaAbstracts/lamgrapami/code/lamgrapami.pl $CFG_file";

	print "$CMD\n"; #<STDIN>;
	my $res = system($CMD);
	print " -> $res\n";

	# now create the data that Fazleh needs
	my $results_file = "results-$maximum_number_of_input_lines-$tau-$whichabstractions.yml";
	my $data = LoadFile($results_file);

	my @blocks = ();

	my $number_of_entities = scalar @{$data->{configuration}->{set_of_entities}};

	foreach my $pattern_omega (@{$data->{frequent_patterns}}){

		my $seen_entities = {};
		$seen_entities->{$_->{"?v0"}} = 1 foreach @{$pattern_omega->{omega}};
		my $triple_pattern = $pattern_omega->{pattern}->[1];

		push(@blocks, {
			number_of_variables => -1 + scalar keys %{$pattern_omega->{omega}->[0]},
			triple_pattern => $triple_pattern,
			entities => $entities
		});
	}

	my $json = JSON->new->allow_nonref;
	$json = $json->pretty(1);
	my $json_text = $json->encode(\@blocks);
	my $json_file = "results-$maximum_number_of_input_lines-$tau-$whichabstractions.json";
	open(OUT,">$json_file");
	print OUT $json_text;
	close OUT;


}
