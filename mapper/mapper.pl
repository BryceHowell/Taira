
$filein=$ARGV[0];


if ($filein =~ /(.*)\.map$/) {
   $fileout=$1.".txt"; } else { die("map files end in .map"); }

#print $filein;
#print $fileout;

open(FHI, "<$filein") || die "cannot open < $filein: $!";
open(FHO, ">$fileout") || die "cannot open > $fileout: $!";
 

%field=();

%colormap=();

$colormap{" "}="NADA";
$colormap{"b"}="DBR";
$colormap{"x"}="DKGR";
$colormap{"u"}="DKGY";
$colormap{"k"}="BLK";
$colormap{"l"}="BLK";
$colormap{"."}=".DGY";
$colormap{"y"}="YEL";
$colormap{"r"}="RED";
$colormap{"o"}="ORN";
$colormap{"C"}="CYN";


$a=0;$b=0;
while ($line=<FHI>) {
  for ($a=0; $a<20; $a++) {
    $item=substr($line,$a,1);
    $field{$a.",".$b}=$item;
    }
  $b++;
  }

sub printfield {
my $a,$b;
for ($b=0; $b<12; $b++) {
  for ($a=0; $a<20; $a++) {
    print $field{$a.",".$b};
    } print "\n";}
 }

#printfield();

## scan for first col. expand right. expand down. record and mark. 
$flag=1;
while ($flag==1) {
  $flag=0; $x=0; $y=0; $c="";
  for ($b=0; $b<12; $b++) {
    for ($a=0; $a<20; $a++) {
      $ctest=$field{$a.",".$b};
      if ($ctest ne " " && $ctest ne "!")  { $flag=1; $c=$field{$a.",".$b}; $x=$a; $y=$b; last; }
      } 
    if ($flag==1) { last;}
    }

  if ($flag==0) {last;}

  if ($flag) {
    #widen right
    for ($i=$x; $i<20; $i++) { if ($field{$i.",".$y} ne $c) { last;} }
    $w=$i-$x;
    # expand down
    $sw=0;
    for ($j=$y+1; $j<12; $j++) {
      for ($i=$x; $i<$x+$w; $i++) {
        if ($field{$i.",".$j} ne $c) { $sw=1; last;} }
      if ($sw==1) { last;}
      }
    $h=$j-$y;
    ## mark
    for ($a=$x; $a<$x+$w; $a++) {
      for ($b=$y; $b<$y+$h; $b++) {
        $field{$a.",".$b}="!"; }
      }
    }
 
  #print "($x,$y) = ($w,$h)\n";
  $wide=8*$w; $high=16*$h;
  $x=8*$x; $y=16*$y; $z=0; $col=$colormap{$c};
  if ($col =~ /^\.(.+)/ ) { $col=$1; $z=-1; } 
  #print $wide.$high.$x.$y.$col."\n";
  $tag="<brush x=\"".$x."\" y=\"".$y."\"";
  $tag.=" z=\"".$z."\" width=\"".$wide."\" height=\"".$high."\"";
  $tag.=" color=\"".$col."\" />\n";
  #print "<brush x=\"".$x."\" y=\"".$y."\" z=\"0\" width=\"".$wide."\" height=\"".$high."\" color=\"".$col."\" />\n"; 

  print $tag;
  print FHO $tag;

  }

close(FHI);
close(FHO);
