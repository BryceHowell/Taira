@lowerUL=();
@lowerUR=();
@lowerDL=();
@lowerDR=();
my $CAVES,$U,$L,$R,$D,$C;
open(CAVES,"<","pit.map");
open(U,">","pit0.map");
open(L,">","pit1.map");
open(C,">","pit2.map");
open(R,">","pit3.map");
open(D,">","pit4.map");


for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print U substr($line,21,20)."\n";
 }

$line=<CAVES>;

for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print L substr($line,0,20)."\n";
 print C substr($line,21,20)."\n";
 print R substr($line,42,20)."\n";
 }

$line=<CAVES>;

for($i=0; $i<12; $i++) {
 $line=<CAVES>;
 print D substr($line,21,20)."\n";
 }


close(CAVES);
close(U);
close(L);
close(C);
close(R);
close(D);
