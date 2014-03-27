* Read in statistics on 2013 offensive performance of non-pitching major league baseball players;
PROC IMPORT DATAFILE="/home/ssouradeep/Baseball player stats for 2013.xlsx"
		    OUT=baseball
		    DBMS=XLSX
		    REPLACE;
			RANGE='2013 MLB statistics$A1:T144';
RUN;

* Tentatively decide on number of factors to retain;
PROC FACTOR   DATA=baseball SCREE;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Principal Components Analysis (the default) -- Baseball Data';
RUN;
* Allow for uniqueness;
PROC FACTOR   DATA=baseball SCREE METHOD=principal PRIORS=smc;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Principal factors with squared multiple correlations as prior communality estimates -- Baseball Data';
RUN;
* Try to interpret 4 factors;
PROC FACTOR   DATA=baseball METHOD=principal PRIORS=smc NFACTORS=4 NPLOT=2;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Principal factors with SMC for communality, 4 factors -- Baseball Data';
RUN;
* Rotate 4 factors to improve interpretability - simplify columns of factor loadings;
PROC FACTOR   DATA=baseball METHOD=principal PRIORS=smc NFACTORS=4 NPLOT=2 ROTATE=varimax;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Principal factors with SMC for communality, 4 factors, VARIMAX rotation -- Baseball Data';
RUN;
* Rotate 4 factors to improve interpretability - simplify rows of factor loadings;
* Save players' factor scores to output dataset;
PROC FACTOR   DATA=baseball METHOD=principal PRIORS=smc NFACTORS=4 NPLOT=2 ROTATE=quartimax OUT=baseball_factors;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Principal factors with SMC for communality, 4 factors, QUARTIMAX rotation -- Baseball Data';
RUN;

* The maximum likelihood method of factor extraction permits testing the number of factors, among other things,
  but requires joint normal distribution of the data and permits some anomalies;
* Rotate 4 factors to improve interpretability - simplify rows of factor loadings;
PROC FACTOR   DATA=baseball METHOD=ML PRIORS=smc NFACTORS=4 NPLOT=2 ROTATE=varimax;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Maximum likelihood factors with SMC for communality, 4 factors, VARIMAX rotation -- Baseball Data';
RUN;
* Fix anomaly;
PROC FACTOR   DATA=baseball METHOD=ML PRIORS=smc NFACTORS=4 NPLOT=2 ROTATE=varimax ULTRAHEYWOOD;
  VAR Batting_average--On_base_plus_slug_pct;
  TITLE 'Maximum likelihood factors with SMC for communality, 4 factors, VARIMAX rotation -- Baseball Data';
RUN;



