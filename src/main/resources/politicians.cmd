cat instance-types_en.nt | grep "<http://dbpedia.org/ontology/Person>" | sed --expression 's/ <http:\/\/www.w3.*//' > persons.txt



cat instance-types_en.nt | grep "<http://dbpedia.org/ontology/Flim>" | sed --expression 's/ <http:\/\/www.w3.*//' > flims.txt


cat instance-types_en.nt | grep "<http://dbpedia.org/ontology/Mountain>" | sed --expression 's/ <http:\/\/www.w3.*//' > Mountain.txt

