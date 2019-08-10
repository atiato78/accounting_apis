/*SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;
Select * from ENG_DriveTraffic;*/

/*drop table invoices;

CREATE TABLE invoices (
  id number(9) NOT NULL,
  operator_id number(4) NOT NULL,
  bank_account_id number(4) NOT NULL,
  invoice_number varchar(60) NOT NULL,
  currency varchar(10) NOT NULL,
  initial_payment DOUBLE PRECISION NOT NULL,
  invoice_amount DOUBLE PRECISION NOT NULL,
  invoice_tax DOUBLE PRECISION NOT NULL,
  invoice_discount DOUBLE PRECISION NOT NULL,
  from_date date NOT NULL,
  to_date date NOT NULL,
  status varchar(15) DEFAULT ('unpaid'),
  log_date DATE DEFAULT (sysdate),
  username varchar(40) NOT NULL,
  is_approved varchar(3) DEFAULT ('no')
) ;

drop table bill_cycle;

create table bill_cycle (
id number(9) NOT NULL,
operator_id number(9) NOT NULL,
from_date date,
to_date date,
predict_start_date date,
predict_end_date date,
log_date timestamp,
username varchar(50),
is_predicted varchar(10)
);

drop table predict_revenue;


create table predict_revenue ( 
    id number(9) NOT NULL,
 operator_id number(9) NOT NULL, 
 from_date date, 
 to_date date,
  future_span number(6),
   log_date DATE DEFAULT (sysdate), 
   username varchar(50),
   predicted_amount DOUBLE PRECISION NOT NULL );
   
   
insert into bill_cycle (id,operator_id,from_date,to_date,predict_start_date,predict_end_date,username,is_predicted) values (1,2,'01-OCT-2019',sysdate,'01-NOV-2019','30-NOV-2019','atia','no');
INSERT INTO invoices (id,operator_id, bank_account_id, invoice_number, currency, initial_payment, invoice_amount, invoice_tax, invoice_discount, from_date, to_date, status, log_date, username, is_approved) VALUES (1,50, 2, '10234888', 'USD', 0, 3800000, 0, 10, TO_DATE('2019-12-01', 'YYYY-MM-DD'), to_date('2019-12-31','YYYY-MM-DD'), 'unpaid', sysdate, 'admin', 'no');
commit;
*/
commit;