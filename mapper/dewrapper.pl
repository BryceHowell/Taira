@lowerUL=();
@lowerUR=();
@lowerDL=();
@lowerDR=();
my $CAVES,$UL,$UR,$DL,$DR;
open(CAVES,"<","lowercave.map");
open(UL,">","cave0.map");
open(UR,">","cave1.map");
open(DL,">","cave2.map");
open(DR,">","cave3.map");


for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print UL substr($line,0,20)."\n";
 print UR substr($line,21,20)."\n";
 }

$line=<CAVES>;

for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print DL substr($line,0,20)."\n";
 print DR substr($line,21,20)."\n";
 }

close(CAVES);
close(UL);
close(UR);
close(DL);
close(DR);
