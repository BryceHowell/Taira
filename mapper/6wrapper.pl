@lowerUL=();
@lowerUR=();
@lowerUF=();
@lowerDL=();
@lowerDR=();
@lowerDR=();
my $CAVES,$UL,$UR,$DL,$DR,$UF,$DF;
open(CAVES,"<","redlabyrinth.map");
open(UL,">","redl0.map");
open(UR,">","redl1.map");
open(UF,">","redl2.map");
open(DL,">","redl3.map");
open(DR,">","redl4.map");
open(DF,">","redl5.map");


for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print UL substr($line,0,20)."\n";
 print UR substr($line,21,20)."\n";
 print UF substr($line,42,20)."\n";
 }

$line=<CAVES>;

for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print DL substr($line,0,20)."\n";
 print DR substr($line,21,20)."\n";
 print DF substr($line,42,20)."\n";
 }

close(CAVES);
close(UL);
close(UR);
close(UF);
close(DL);
close(DR);
close(DF);
