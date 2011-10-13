my @pages;
my %styles;
my %images;
my $baseurl = "http://127.0.0.1/foswiki/bin/view/Mundo";
my $wren = 1;
my $firstpage = 1;

my $outdir = '../../docs/tutorial';

`rm wget-log.txt`;
mkdir 'tmp';
mkdir $outdir;
mkdir $outdir.'/images';
mkdir $outdir.'/style';

open(ALLFH, ">$outdir/book.html");
getpage("MCJavaDoc", 1, "tutorial/");
foreach $p (@pages)
{
  next if ($p eq 'MundoCore');
  getpage($p, 0, "");
}
print ALLFH "</body></html>\n";
close(ALLFH);
`mv $outdir/MCJavaDoc.html $outdir/../index.html`;

foreach $s (keys %styles)
{
  $url = $styles{$s};
  `wget -O $outdir/style/$s http://127.0.0.1/$url 2>&1 >>wget-log.txt`;
}
foreach $s (keys %images)
{
  $url = $images{$s};
  `wget -O $outdir/images/$s http://127.0.0.1/$url 2>&1 >>wget-log.txt`;
}

sub getpage
{
  my $pagename = shift;
  my $rec = shift;
  my $prefix = shift;

  my $fn = "tmp/".$pagename.".html";
  print "GET ", $fn, "\n";
  `wget -O $fn $baseurl/$pagename?cover=print 2>&1 >>wget-log.txt`;

  open(OUTFH, ">$outdir/$pagename.html");
  open(INFH, "<$fn");
  while(<INFH>)
  {
#    if ($_=~/<h2>/)
#    {
#      print $_;
#    }
    next if ($_=~/<base/);
    if ($_=~/<body/)
    {
      if ($wren == 1) {
	print ALLFH $_;
      }
      $wren = 1;
      print OUTFH $_;
      next;
    }
    if ($_=~/<!-- \/foswikiTopic-->/)
    {
      $wren = 0;
      print OUTFH "</div></div><!-- /patternContent-->\n";
      print OUTFH "</div></div></div></div></div></div></div></div>\n";
      print OUTFH "</body></html>\n";

      print ALLFH "</div></div><!-- /patternContent-->\n";
      print ALLFH "</div></div></div></div></div></div></div></div>\n";
      last;
    }
    elsif ($_=~/(.*)<a href="(.*)">(.*)<\/a>(.*)/)
    {
      $pre = $1;
      $url = $2;
      $text = $3;
      $post = $4;
      if ($url=~/^\/foswiki\/bin\/view\/Mundo\/(\w+)$/)
      {
	if ($text eq 'MundoCore') {
	  print OUTFH $pre, $text, $post, "\n";
	  print ALLFH $pre, $text, $post, "\n";
	} else {
          $page = $1;
          print OUTFH $pre, "<a href=\"", $prefix.$page, ".html\">", $text, "</a>", $post, "\n";
          print ALLFH $pre, "<a href=\"", $page, ".html\">", $text, "</a>", $post, "\n";
          if ($rec) {
            push(@pages, $page);
          }
	}
      }
      else
      {
	print OUTFH $_;
        if ($wren) {
          print ALLFH $pre, $text, $post, "\n";
        }
      }
    }
    elsif ($_=~/\@import url\(.\/(.*)\/([^\/]+\.css).\);/)
    {
      $styles{$2}=$1.'/'.$2;
      print OUTFH "\@import url('", $prefix, "style/$2');\n";
      if ($wren) {
        print ALLFH "\@import url('style/$2');\n";
      }
    }
    elsif ($_=~/(.*)src="([^"]+)"(.*)/)
    {
      $pre = $1;
      $url = $2;
      $post = $3;
      if ($url=~/^(.*)\/([^\/]+)$/)
      {
        $images{$2} = $1.'/'.$2;
        print OUTFH $pre, "src=\"images/", $2, "\"", $post, "\n";
        if ($wren) {
          print ALLFH $pre, "src=\"images/", $2, "\"", $post, "\n";
        }
      }
      else
      {
        print OUTFH $_;
        if ($wren) {
          print ALLFH $_;
        }
      }
    }
    else
    {
      print OUTFH $_;
      if ($wren) {
	if ($_=~/<h1>/) {
          if (!$firstpage) {
            $_=~s/<h1>/<h1 style="page-break-before:always">/;
	  }
	  $firstpage = 0;
	}
        print ALLFH $_;
      }
    }
  }
  close(INFH);
  close(OUTFH);
}
