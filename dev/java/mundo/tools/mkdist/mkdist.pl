$version="1.0.0-RC1";

# Determine platform
if (defined $ENV{'OS'} and $ENV{'OS'} eq "Windows_NT") {
  $os_windows=1;
} else {
  $os_unix=1;
}

mkdir("dist");
open(FH, ">dist/version.txt");
print FH "MundoCore Java ", $version, "\n";
close(FH);

mkdir("dist/bin");
copy("tools/mkdist/configure.bat", "dist");
copy("tools/mkdist/configure.sh", "dist");
copy("bin/mcc-linux", "dist/bin");
copy("bin/mcc-mac-x86", "dist/bin");
copy("bin/mcc.exe", "dist/bin");
#copy("bin/libmundoext.jnilib", "dist/bin");

copy("license.txt", "dist");
copy("docs/README.txt", "dist");

mkdir("dist/lib");
mkdir("dist/lib/se");
copy("lib/se/mundocore.jar", "dist/lib/se");
copy("lib/se/sources.zip", "dist/lib/se");
mkdir("dist/lib/android");
copy("lib/android/mundocore.jar", "dist/lib/android");
copy("lib/android/sources.zip", "dist/lib/android");

mkdir("dist/tools");
copy("tools/configure.jar", "dist/tools");
copy("apps/inspect/inspect.jar", "dist/tools");

#mkdir("dist/config");
#open(FH, ">dist/config/build.properties");
#print FH "# MundoCore root directory\r\n";
#print FH "mcroot=c:/mundocore-$version\r\n";
#print FH "#\r\n";
#print FH "# Interface repository\r\n";
#print FH "mcinterfaces=\${mcroot}/interfaces\r\n";
#print FH "#\r\n";
#print FH "# Precompiler\r\n";
#print FH "mcc=\${mcroot}/bin/mcc.exe\r\n";
#print FH "#mcc=\${mcroot}/bin/mcc-mac-x86\r\n";
#print FH "#mcc=\${mcroot}/bin/mcc-linux\r\n";
#print FH "#\r\n";
#close(FH);

mkdir("dist/docs");
#copy("docs/tutorial.pdf dist/docs");
#copy("docs/mundocore-0.9.9.chm dist/docs");
#copy("docsrc/migration08.html dist/docs");
copy("docs/index.html", "dist/docs");
copytree("docs/api", "docs/api", ".*");
copytree("docs/tutorial", "docs/tutorial", ".*");
copy("docs/tutorial.pdf", "dist/docs");

#mkdir("dist/docs/tutorial");
#copy("docs/tutorial/*.html dist/docs/tutorial");
#copy("docs/tutorial/*.gif dist/docs/tutorial");

copytree("../interfaces", "interfaces", "\.xml\$");

processListfiles();

#copy("docsrc/node.conf.bcast.xml dist/samples/chat/simple/node.conf.xml");
#copy("docsrc/node.conf.bcast.xml dist/apps/inspect/node.conf.xml");

# build archive
`rm -r mundocore-$version`;
`mv dist mundocore-$version`;
unlink "mundocore-java-$version.zip";
`zip -r mundocore-java-$version.zip mundocore-$version`;
#unlink "mundocore-java-$version.tar.bz2";
#`tar cvf mundocore-java-$version.tar mundocore-$version`;
#`bzip2 mundocore-java-$version.tar`;
#`rm -r mundocore-$version`;

sub copy
{
  my $src=shift;
  my $dest=shift;
  if ($os_windows)
  {
    $src=~s/\//\\/g;
    $dest=~s/\//\\/g;
    `copy $src $dest`;
  }
  else
  {
    `cp $src $dest`;
  }
}

sub xcopy
{
  my $src=shift;
  my $dest=shift;
  if ($os_windows)
  {
    $src=~s/\//\\/g;
    $dest=~s/\//\\/g;
    `xcopy /s $src $dest`;
  }
  else
  {
    `cp -r $src $dest`;
  }
}

sub processListfiles
{
  processDir(".");
}

sub processDir
{
  my $dn=shift;
  my $fn;
  my $an;
  my @dirs;
  opendir(DIR, $dn);
  while ($fn=readdir(DIR))
  {
    next if ($fn=~/^\./);
    $an=$dn."/".$fn;
    if (-d $an)
    {
      push(@dirs, $fn);
    }
    elsif ($fn eq "dist.lst")
    {
      processListfile($dn, $fn);
    }
  }
  foreach $fn (@dirs)
  {
    processDir($dn."/".$fn);
  }
}

sub processListfile
{
  my $dn=shift;
  my $fn=shift;
  $dn=substr($dn, 2);
  makedirs($dn);
  open(INFH, "$dn/$fn");
  while(<INFH>)
  {
    chomp;
    copy("$dn/$_", "dist/$dn");
  }
  close(INFH);
}

sub makedirs
{
  my $dn=shift;
  my @dirs=split("/", $dn);
  my $an="dist";
  foreach $dn (@dirs)
  {
    $an=$an.'/'.$dn;
    mkdir($an);
  }
}

sub copytree
{
  my $srcroot=shift;
  my $destroot=shift;
  my $pat=shift;
  opendir(DIR, $srcroot);
  my $fn;
  my @dirs=();
  while ($fn=readdir(DIR))
  {
    next if ($fn=~/^\./);
    my $an=$srcroot.'/'.$fn;
    if (-d $an)
    {
      push(@dirs, $fn);
    }
    else
    {
      if ($fn=~/$pat/)
      {
        makedirs($destroot);
        copy($an, 'dist/'.$destroot);
      }
    }
  }
  closedir(DIR);
  foreach $fn (@dirs)
  {
    copytree($srcroot.'/'.$fn, $destroot.'/'.$fn, $pat);
  }
}
