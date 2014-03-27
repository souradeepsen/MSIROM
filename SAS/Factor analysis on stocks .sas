PROC IMPORT DATAFILE="/home/ssouradeep/FactorAnalysis/Stock data.xlsx"
		    OUT=stocks
		    DBMS=XLSX
		    REPLACE;
RUN;
DATA STOCKS;
  SET stocks;
  IF        _N_ LE  5 THEN industry='Oil     ';
    ELSE IF _N_ LE 13 THEN industry='Drug    ';
    ELSE                   industry='Computer';
RUN;
PROC PRINCOMP DATA=STOCKS;
  VAR P_E profit growth;
  TITLE "PCA on 19 Stocks and 3 Variables"; 
RUN;
* Principal components analysis as a "special case" of factor analysis;
* The default options are METHOD=principal PRIORS=one MINEIGEN=1;
PROC FACTOR DATA=stocks METHOD=principal PRIORS=one MINEIGEN=0;
  VAR P_E profit growth;
  TITLE "Factor Analysis on 19 Stocks and 3 Variables"; 
RUN;
* Three follow-up orthonormal rotations that attempt to improve interpretability of factors;
PROC FACTOR DATA=stocks METHOD=principal PRIORS=one MINEIGEN=0 ROTATE=varimax;
  VAR P_E profit growth;
  TITLE "Factor Analysis on 19 Stocks and 3 Variables, followed by VARIMAX rotation"; 
RUN;
PROC FACTOR DATA=stocks METHOD=principal PRIORS=one MINEIGEN=0 ROTATE=quartimax;
  VAR P_E profit growth;
  TITLE "Factor Analysis on 19 Stocks and 3 Variables, followed by QUARTIMAX rotation"; 
RUN;
PROC FACTOR DATA=stocks METHOD=principal PRIORS=one MINEIGEN=0 ROTATE=equamax;
  VAR P_E profit growth;
  TITLE "Factor Analysis on 19 Stocks and 3 Variables, followed by EQUAMAX rotation"; 
RUN;
* Non-PCA factor analysis;
PROC FACTOR DATA=stocks METHOD=principal PRIORS=SMC NFACTORS=2;
  VAR P_E profit growth;
  TITLE "A 'regular' factor analysis on 19 Stocks and 3 Variables";
RUN;

