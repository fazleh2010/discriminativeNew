#!/usr/bin/perl -w
use strict;
use URL::Encode qw(url_encode_utf8 url_decode_utf8);
use YAML qw(Dump DumpFile);

# TODO: record the number of the first sentence in which "democratic" occurs
# TODO: only consider tokens that exactly match "democratic" (and not, e.g., "democratically")

my $entityfile = "/opt/massive_correlation/data/dbpedia-2015-04/politicians.txt";
my $entities = [];
my $cnt = 0;
open(DAT,"<$entityfile");
while(defined(my $line=<DAT>) and ++$cnt < 200000000){ # TODO: remove that limitation
        $line =~ s/\n//;
        push(@{$entities}, $line);
}
close DAT;

my $democratic = {};

foreach my $entity (@{$entities}){
	$entity =~ s/\A<//;
	$entity =~ s/>\Z//;

	$entity =~ s/.*resource\///;
	my $entity_enc = url_encode_utf8($entity);

	my $file_found = 0;
	FEF: foreach my $file (glob("/opt/massive_correlation/data/EnglishDBpediaAbstracts/parsed/$entity_enc%2Fabstract*")){
		print " $entity -> $file\n"; #<STDIN>;
		$file_found = 1;
		open(DAT,"<$file");
		while(defined(my $line=<DAT>)){
			if($line =~ m/democratic/){
				$democratic->{$entity} = 1;
				next FEF;
			}
		}
	}

	if($file_found and not defined $democratic->{$entity}){
		$democratic->{$entity} = 0;
	}

	if(not $file_found){
		print "No abstract found for entity $entity\n";
		$democratic->{$entity} = "unknown";
	}

}

print Dump $democratic;
DumpFile("politicians_with_democratic.yml", $democratic);

