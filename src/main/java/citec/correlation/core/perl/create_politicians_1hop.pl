#!/usr/bin/perl -w
use strict;
use YAML qw(Dump);
use Storable;
use List::Util 'shuffle';
use Clone qw(clone);


my $nt_file = "/opt/massive_correlation/data/dbpedia-2015-04/DI1I4M.nt";

# load lists of entities

my $entity_lists = [];
my $cntf=0;

my $entities = [];
open(DAT,"</opt/massive_correlation/data/dbpedia-2015-04/politicians.txt");
while(defined(my $line=<DAT>)){
        $line =~ s/\n//;
	$line =~ s/\A<//;
	$line =~ s/>\Z//;
        push(@{$entities}, $line);
}
close DAT;

push(@{$entity_lists}, { entities => $entities });

# load DBpedia data

my $term_to_ID = {};
my $ID_to_term = {};
my $last_ID = -1;
my $out_rdftype = {};
my $out_nonrdftype = {};
my $out_literal = {};
my $in_nonrdftype = {};

my $cnt = 0;
open(DAT,"<$nt_file");
while(defined(my $line=<DAT>)){
	last if ++$cnt > 10000000000000;
	print "$cnt\n" if $cnt % 100000 == 0;
	next if $line =~ m/\A#/;
	$line =~ s/\n//;
	if($line =~ m/<(.*)> <http:\/\/www.w3.org\/1999\/02\/22-rdf-syntax-ns#type> <(.*)> \.\Z/){
		my $ID1 = &ID($1);
		my $ID2 = &ID($2);
		$out_rdftype->{$ID1}->{$ID2} = 1;
	} elsif($line =~ m/<(.*)> <(.*)> <(.*)> \.\Z/){
		my $ID1 = &ID($1);
                my $ID2 = &ID($2);
		my $ID3 = &ID($3);
		$out_nonrdftype->{$ID1}->{$ID2}->{$ID3} = 1;
		$in_nonrdftype->{$ID3}->{$ID2}->{$ID1} = 1;
	} else {
		if($line =~ m/<(.*)> <(.*)> (.*) \.\Z/){
			$out_literal->{&ID($1)}->{&ID($2)}->{&ID($3)} = 1;
		} else {
			print "unexpected line format: $line\n"; <STDIN>;
		}
	}
}
close DAT;

#print "wait\n"; #<STDIN>;
#print Dump {
#	out_rdftype => scalar keys %{$out_rdftype},
#	out_nonrdftype => scalar keys %{$out_nonrdftype},
#	out_literal => scalar keys %{$out_literal},
#	in_nonrdftype => scalar keys %{$in_nonrdftype},
#};

# create n-hop-covers

foreach my $block (@{$entity_lists}){

	my $triples = {};
	my $nodes_to_be_explored = {};
	$nodes_to_be_explored->{$_} = 1 foreach @{$block->{entities}};
	my $explored_nodes = {};
	my $node_reached_via_rdftype = {};

	my $cnt_triples = {};

	foreach my $hop (1..1){

	        my $new_nodes_to_be_explored = {};

		foreach my $node (keys %{$nodes_to_be_explored}){
			$explored_nodes->{$node} = 1;

			if(exists $out_rdftype->{$node}){
				foreach my $object (keys %{$out_rdftype->{$node}}){
					$cnt_triples->{"out-rdftype-$hop"}++;
					$triples->{$node}->{"http://www.w3.org/1999/02/22-rdf-syntax-ns#type"}->{$object} = "out-rdftype-$hop";
					$new_nodes_to_be_explored->{$object} = 1
						if not exists $explored_nodes->{$object};
					$node_reached_via_rdftype->{$object} = 1;
				}
			}

			if(exists $out_literal->{$node}){
				foreach my $property (keys %{$out_literal->{$node}}){
					foreach my $object (keys %{$out_literal->{$node}->{$property}}){
						$cnt_triples->{"out-literal-$hop"}++;
						$triples->{$node}->{$property}->{$object} = "out-literal-$hop";
					}
				}
			}

			if(exists $out_nonrdftype->{$node}){
				foreach my $property (keys %{$out_nonrdftype->{$node}}){
					foreach my $object (keys %{$out_nonrdftype->{$node}->{$property}}){
						$cnt_triples->{"out-nonrdftype-$hop"}++;
						$triples->{$node}->{$property}->{$object} = "out-nonrdftype-$hop";
						$new_nodes_to_be_explored->{$object} = 1
							if not exists $explored_nodes->{$object}; 
					}
				}
			}

			if(not exists $node_reached_via_rdftype->{$node} and exists $in_nonrdftype->{$node}){
				foreach my $property (keys %{$in_nonrdftype->{$node}}){
					foreach my $subject (keys %{$in_nonrdftype->{$node}->{$property}}){
						$cnt_triples->{"in-nonrdftype-$hop"}++;
						$triples->{$subject}->{$property}->{$node} = "in-nonrdftype-$hop";
						$new_nodes_to_be_explored->{$subject} = 1
							if not exists $explored_nodes->{$subject};
					}
				}
			}
		}

		print Dump {
			"after_hop_$hop" => {
				cnt_triples => $cnt_triples,
				new_nodes_to_be_explored => scalar keys %{$new_nodes_to_be_explored},
	        	        node_reached_via_rdftype => scalar keys %{$node_reached_via_rdftype},
			}
		}; #<STDIN>;

		$nodes_to_be_explored = $new_nodes_to_be_explored;
		
		if($hop eq "2"){
			&store_nhopcover($triples, $block, $hop);
		}
	}

        &store_nhopcover($triples, $block, 1);

	#print "STDIN\n"; <STDIN>;
}

sub store_nhopcover {
	my ($triples, $block, $hop) = @_;

	my $filename = "politicians_1hop.nt";
	
	open(DAT,">$filename");
        foreach my $S (sort keys %{$triples}){
                foreach my $P (sort keys %{$triples->{$S}}){
                        foreach my $O (sort keys %{$triples->{$S}->{$P}}){
                                my $T = $triples->{$S}->{$P}->{$O};
                                if($T=~ m/\Aout-literal/){
                                        print DAT "<$S> <$P> $O .\n";
                                } else {
                                        print DAT "<$S> <$P> <$O> .\n";
                                }
                        }
                }
        }
	close DAT;

	print " > $filename\n";

	#my $cmd = "gzip $filename";
	#system($cmd);

}

sub ID {
	my $term = shift;
	return $term;
	
	return $term_to_ID->{$term} if exists $term_to_ID->{$term};
	$last_ID++;
	$term_to_ID->{$term} = $last_ID;
	$ID_to_term->{$last_ID} = $term;
	return $last_ID;
}

sub select_entities { 
        my @array = @{$_[0]};
	my $sampleToN = $_[1];

        my @shuffled_indexes = shuffle(0..$#array);
        #print Dump { shuffled_indexed => \@shuffled_indexes };

        my @pick_indexes = @shuffled_indexes[ 0 .. $sampleToN - 1 ];
        #print Dump { pick_indexes => \@pick_indexes };

        my @set = @array[ @pick_indexes ];

        return \@set;
}


