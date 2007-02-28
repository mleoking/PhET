MAIN=NanoXML-Lite
COMPONENTS=NanoXML-Lite.tex defs.tex introduction.tex retrieving_data.tex
PDFLATEX=/usr/local/teTeX/bin/powerpc-apple-darwin-current/pdflatex

all: $(MAIN).pdf

clean:
	rm -f $(MAIN).pdf $(COMPONENTS:.tex=.aux) $(MAIN).log $(MAIN).toc

$(MAIN).pdf: $(COMPONENTS)
	sed "s/ModDate (D:[0-9]*)/ModDate (D:`date +'%Y%m%d%H%M%S'`)/" \
	        < $(MAIN).tex >$(MAIN).tmp
	mv $(MAIN).tmp $(MAIN).tex
	$(PDFLATEX) $(MAIN).tex
	$(PDFLATEX) $(MAIN).tex >/dev/null 2>&1
	$(PDFLATEX) $(MAIN).tex >/dev/null 2>&1
