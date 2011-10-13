#!/usr/bin/perl
# 20.10.2009 Erwin Aitenbichler
#
# This is a support script for bitten to run unit tests. It implements the
# following functionalities that bitten does not directly provide:
#
# 1. Automatically scans the directory for tests
# 2. Continues running tests even when tests fail or do not compile.
#    If a test does not compile, a junit-report file is generated
# 3. Collects all reports into a single directory such that all
#    results can be displayed in a single table in trac

use File::Copy;

$dir=".";
$dir=$1 if ($0=~/^(.*)\/[^\/]+$/);
$hostname=`hostname`;
chomp $hostname;

mkdir("$dir/reports");
opendir(DIR, $dir);
while($fn=readdir(DIR))
{
  next if ($fn=~/^\./);
  $an=$dir."/".$fn;
  if (-d $an)
  {
    if (-f $an."/build.xml")
    {
      runTest($fn);
    }
  }
}
closedir(DIR);

sub runTest
{
  my $dn = shift;
  print "running $dn\n";
  chdir($dn);
  system("ant");
  $n=0;
  opendir(DIR2, "reports");
  while($fn=readdir(DIR2))
  {
    if ($fn=~/TEST-(.*)\.xml/)
    {
      $tn=$1;
      print "copy reports/$fn  to ../reports/TEST-$dn-$tn.xml\n";
      copy("reports/$fn", "../reports/TEST-$dn-$tn.xml");
      $n++;
    }
  }
  closedir(DIR2);
  if ($n==0)
  {
    open(FH, ">$dir/reports/TEST-$dn.xml");
    print FH "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
    print FH "<testsuite name=\"$dn\" tests=\"1\" errors=\"0\" failures=\"1\" hostname=\"$hostname\" time=\"1.0\"\>\n";
    print FH "<testcase classname=\"$dn\" name=\"$dn\" time=\"1.0\">\n";
    print FH "<error message=\"build failed\" type=\"junit.framework.AssertionFailedError\">build failed</error>\n";
    print FH "</testcase>\n";
    print FH "<system-out><![CDATA[]]></system-out>\n";
    print FH "<system-err><![CDATA[]]></system-err>\n";
    print FH "</testsuite>\n";
    close(FH);
  }
  chdir("..");
}
