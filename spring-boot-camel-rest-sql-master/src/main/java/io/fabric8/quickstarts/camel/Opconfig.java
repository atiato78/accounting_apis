/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.fabric8.quickstarts.camel;

import java.sql.Date;
import java.sql.Timestamp;

import org.joda.time.DateTime;

//create table Payment (
//op_code varchar(10),
//invoice_number varchar(60),
//ref_invoice varchar(60),
//amount double,
//currency varchar(10),
//status integer,
//payment_type integer,
//payment_date date,
//bank_code varchar(20)
//);



public class Opconfig {
    private String op_code;
   
    private double coe_ratio;
    private String currency;
    private int op_pay_type;
    private double op_tax;
  
    public String getOp_code() {
        return op_code;
    }

    public void setOp_code(String op_code) {
        this.op_code = op_code;
    }

   

    public double getCoe_ratio() {
        return coe_ratio;
    }

    public void setCoe_ratio(double coe_ratio) {
        this.coe_ratio = coe_ratio;
    }
    
    

  
    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    } 
    
    public int getOp_pay_type() {
        return op_pay_type;
    }

    public void setOp_pay_type(int op_pay_type) {
        this.op_pay_type = op_pay_type;
    }
    
   
    public double getOp_tax() {
        return op_tax;
    }

    public void setOp_tax(double op_tax) {
        this.op_tax = op_tax;
    }
}
