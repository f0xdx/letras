print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
print "<build xmlns:sh=\"http://bitten.edgewall.org/tools/sh\"\n";
print "       xmlns:svn=\"http://bitten.edgewall.org/tools/svn\"\n";
print "       xmlns:java=\"http://bitten.edgewall.org/tools/java\">\n";
print "  <step id=\"svn checkout\">\n";
print "    <svn:checkout url=\"https://trac.tk.informatik.tu-darmstadt.de/svn/projects/mundo/mundocore-java\"\n";
print "                  username=\"tracbuild\" password=\"Tb1773n\" path=\"\${path}\" revision=\"\${revision}\"/>\n";
print "  </step>\n";
#print "<step id=\"make mcc executable\">\n";
#print "  <sh:exec executable=\"chmod\" args=\"777 ${basedir}/bin/mcc-linux\"/>\n";
#print "</step>\n";
print "  <step id=\"compile library\">\n";
print "    <java:ant file=\"build.xml\"/>\n";
print "  </step>\n";

opendir(DIR, ".");
while($fn=readdir(DIR))
{
  next if ($fn=~/^\./);
  if (-d $fn)
  {
    if (-f $fn."/build.xml")
    {
      testStep($fn);
    }
  }
}
closedir(DIR);

print "</build>\n";

sub testStep
{
  my $dn = shift;
  print "  <step id=\"run unit tests: $dn\">\n";
  print "    <java:ant file=\"tests/junit/$dn/build.xml\"/>\n";
  print "  </step>\n";
  print "  <step id=\"parse junit results: $dn\">\n";
  print "    <java:junit file=\"tests/junit/$dn/reports/TEST-*.xml\" srcdir=\"tests/junit/$dn/src\"/>\n";
  print "  </step>\n";
}
