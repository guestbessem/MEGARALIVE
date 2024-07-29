-------------------------------------------------------- 
-- Exemple de script SQL d'entrepôt de données SQL pour TDW 2.1 
-------------------------------------------------------- 
-- Ce script montre comment créer et alimenter 
-- un entrepôt de données (similaire aux entrepôts de données de TDW 1.x) 
-- à partir des tables "à plat" de TDW 2.1. 
-- Ce script peut être exécuté via DB2 UDB CLP : 
-- db2 -tvf myscript 
--------------------------------------------------------  
-- 1. Créez une table "à plat" à partir de TDW 2.1 (simulé). 
-- Une ligne par heure par système Windows.  
drop table itmuser."Win_System_H";  
create table itmuser."Win_System_H" ( 
 WRITETIME                      CHAR( 16 ), 
 "Server_Name"                    CHAR( 64 ), 
 "Operating_System_Type"          CHAR( 16 ), 
 "Network_Address"              CHAR( 16 ), 
 "MIN_%_Total_Privileged_Time"  INTEGER, 
 "MAX_%_Total_Privileged_Time"  INTEGER, 
 "AVG_%_Total_Privileged_Time"  INTEGER, 
 "MIN_%_Total_Processor_Time"   INTEGER, 
 "MAX_%_Total_User_Time"        INTEGER, 
 "AVG_%_Total_User_Time"        INTEGER );  

-- 2. Insérez des exemples de données.  

insert into itmuser."Win_System_H" values ( 
'1050917030000000', 'Primary:WinServ1:NT', 'Windows_2000', '8.53.24.170',
20, 40, 30, 10, 30, 20 );  

insert into itmuser."Win_System_H" values ( 
'1050917040000000','Primary:WinServ1:NT','Windows_2000','8.53.24.170',
20, 40, 30, 10, 30, 20 );  

insert into itmuser."Win_System_H" values (
'1050917030000000','Primary:WinServ2:NT','Windows_2000','8.53.24.171',
20, 40, 30, 10, 30, 20 );  

insert into itmuser."Win_System_H" values (
'1050917040000000','Primary:WinServ2:NT','Windows_2000','8.53.24.171',
  20, 40, 30, 10, 30, 20 );  

-- 3. Créez une table de dimensions pour les hôtes. 
-- Clé primaire : Server_ID, une valeur générée 
-- Autres clés : Server_Name, Network_Address  

drop table itmuser."D_Win_System"; 
create table itmuser."D_Win_System" (   
 "Server_ID" INTEGER GENERATED ALWAYS AS IDENTITY
    PRIMARY KEY NOT NULL,   
 "Server_Name"                    CHAR( 64 ),   
 "Operating_System_Type"          CHAR( 16 ),   
 "Network_Address"                CHAR( 16 ) );  

-- 4. Créez une table des faits survenus toutes les heures pour le système. 
-- Server_ID : clé externe pour D_Win_System  

drop table itmuser."F_Win_System_H"; 
create table itmuser."F_Win_System_H" (   
 WRITETIME                      CHAR( 16 ) NOT NULL,   
 "Server_ID"                    INTEGER NOT NULL,   
 "MIN_%_Total_Privileged_Time"  INTEGER,   
 "MAX_%_Total_Privileged_Time"  INTEGER,   
 "AVG_%_Total_Privileged_Time"  INTEGER,   
 "MIN_%_Total_Processor_Time"   INTEGER,   
 "MAX_%_Total_User_Time"        INTEGER,   
 "AVG_%_Total_User_Time"        INTEGER,   
 constraint SERVID foreign key ("Server_ID")     
  references itmuser."D_Win_System" ("Server_ID")  
);  

-- 5. Procédez à l'insertion dans la table de dimensions.  
-- Insérez uniquement les lignes qui n'existent pas encore.   

insert into itmuser."D_Win_System" (   
 "Server_Name",   
 "Operating_System_Type",   
 "Network_Address" ) 
select   
 "Server_Name",   
 min("Operating_System_Type") as "Operating_System_Type",
 "Network_Address" 
from
  itmuser."Win_System_H" h 
where    
 not exists ( select 1 from     
 itmuser."D_Win_System" d     
 where d."Server_Name" = h."Server_Name"      
 and d."Network_Address" = h."Network_Address"    
 ) 
group by   
 "Server_Name",   
 "Network_Address" 
;  

-- 6. Vérifiez les valeurs de la table de dimensions. 
select * from itmuser."D_Win_System"  
;  

-- 7. Procédez à l'insertion dans la table des faits. 
-- Insérez uniquement les lignes qui n'existent pas encore.   
insert into itmuser."F_Win_System_H" 
select   
 h.WRITETIME   ,   
 d."Server_ID" ,   
 h."MIN_%_Total_Privileged_Time" ,   
 h."MAX_%_Total_Privileged_Time" ,   
 h."AVG_%_Total_Privileged_Time" ,   
 h."MIN_%_Total_Processor_Time"  ,   
 h."MAX_%_Total_User_Time"       ,   
 h."AVG_%_Total_User_Time"        
from   
 itmuser."Win_System_H" h,   
 itmuser."D_Win_System" d 
where d."Server_Name" = h."Server_Name"    
 and d."Network_Address" = h."Network_Address"   
 and not exists ( select 1 from      
 itmuser."F_Win_System_H" f      
  where f.WRITETIME = h.WRITETIME   
  and f."Server_ID" = d."Server_ID"     
  ) 
;  

-- 8. Vérifiez les valeurs de la table des faits. 
select * from itmuser."F_Win_System_H"
;  

-- 9. Répétez les étapes 5, "Insertion dans la table de dimensions", 
-- et 7, Insertion dans la table des faits, sur une base journalière.