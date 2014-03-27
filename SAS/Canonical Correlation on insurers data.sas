*************************************;
** CANONICAL CORRELATION EXAMPLES  **;
*************************************;

* Austin apartment data *;
* Read in the raw data: 9 variables on 60 Austin apartments *;
PROC IMPORT DATAFILE="/home/tomsager/AustinApartmentRent.xls"
		    OUT=apartments
		    DBMS=xls
		    REPLACE;
RUN;
* A simple canonical correlation to show that regression is a special case of CanCorr
  and to introduce some common CanCorr options;
PROC CANCORR DATA=apartments VNAME='RENT' VPREFIX=RENT  WNAME='Area and Baths' WPREFIX=PRED
             VDEP STB  B  T  SEB PROBT SMC OUT=out_apartments;
  VAR rent;
  WITH area bathrooms;
RUN;
* Compare CanCorr with regression on unstandardized variables;
PROC REG DATA=apartments;
  MODEL rent = area bathrooms;
RUN;
* Compare CanCorr with regression on standardized variables;
PROC STDIZE DATA=apartments OUT=std_apts METHOD=STD;
RUN;
PROC REG DATA=std_apts;
  MODEL rent = area bathrooms ;
RUN;

* Life insurer data *;
LIBNAME BA "/home/tomsager/";
DATA Life_Insurers_Success_2011;
  SET BA.Life_Insurers_Success_2011;
RUN; 
** Canonical correlation between a set of 3 "success" measures and 5 features of life insurers **;
PROC CANCORR DATA=Life_Insurers_Success_2011 VNAME='Success' VPREFIX=Success  WNAME='Features' WPREFIX=Pred
             VDEP STB  B  T  SEB PROBT SMC REDUNDANCY OUT=out_insurers;
  VAR Prem_asset_ratio  Return_on_inv_assets  Return_on_capital ;
  WITH Leverage  RBC_ratio  Life_prem_ratio  Annuity_prem_ratio  Health_prem_ratio  Reinsurance_prem_ratio;
RUN;

