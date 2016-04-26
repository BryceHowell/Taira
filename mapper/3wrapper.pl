@lowerUL=();
@lowerUR=();
@lowerUF=();
my $CAVES,$UL,$UR,$UF;
open(CAVES,"<","redlabyrinth.map");
open(UL,">","redl0.map");
open(UR,">","redl1.map");
open(UF,">","redl2.map");


for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print UL substr($line,0,20)."\n";
 print UR substr($line,21,20)."\n";
 print UF substr($line,42,20)."\n";
 }

close(CAVES);
close(UL);
close(UR);
close(UF);
