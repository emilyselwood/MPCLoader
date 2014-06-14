

CREATE TABLE public.minorplanet (
  identifier                        character varying(20)
  ,absolutemagnitude                numeric
  ,slope                            numeric
  ,epoch                            timestamp without time zone
  ,meananomalyepoch                 numeric
  ,argumentofperihelion             numeric
  ,longitudeoftheascendingnode      numeric
  ,inclinationtotheecliptic         numeric
  ,orbitaleccentricity              numeric
  ,meandailymotion                  numeric
  ,semimajoraxis                    numeric
  ,uncertaintyparameter             character varying(20)
  ,reference                        character varying(20)
  ,numberofobservations             integer
  ,numberofoppositions              integer
  ,rmsresidual                      numeric
  ,coarseindicatorofperturbers      character varying(20)
  ,preciseindicatorofperturbers     character varying(20)
  ,computername                     character varying(20)
  ,hexdigitflags                    integer
  ,readabledesignation              character varying(50)
  ,dateoflastobservation            timestamp without time zone
  ,yearoffirstobservation           integer
  ,yearoflastobservation            integer
  ,arclength                        integer
)
WITH (
  OIDS = FALSE
);
ALTER TABLE minorplanet
  OWNER TO loader;
GRANT ALL ON TABLE minorplanet TO loader;