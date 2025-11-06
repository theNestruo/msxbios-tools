call mvn package
java -jar target\msxbiostools.jar -tsv .\bios\reference > bios_reference_list.tsv
java -jar target\msxbiostools.jar .\bios\reference
