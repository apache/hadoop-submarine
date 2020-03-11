--  Licensed to the Apache Software Foundation (ASF) under one or more
--  contributor license agreements. See the NOTICE file distributed with
--  this work for additional information regarding copyright ownership.
--  The ASF licenses this file to You under the Apache License, Version 2.0
--  (the "License"); you may not use this file except in compliance with
--  the License. You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing, software
--  distributed under the License is distributed on an "AS IS" BASIS,
--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  See the License for the specific language governing permissions and
--  limitations under the License.

WITH ws_wh AS
(SELECT
    ws1.ws_order_number,
    ws1.ws_warehouse_sk wh1,
    ws2.ws_warehouse_sk wh2
  FROM web_sales ws1, web_sales ws2
  WHERE ws1.ws_order_number = ws2.ws_order_number
    AND ws1.ws_warehouse_sk <> ws2.ws_warehouse_sk)
SELECT
  count(DISTINCT ws_order_number) AS `order count `,
  sum(ws_ext_ship_cost) AS `total shipping cost `,
  sum(ws_net_profit) AS `total net profit `
FROM
  web_sales ws1, date_dim, customer_address, web_site
WHERE
  d_date BETWEEN '1999-02-01' AND
  (CAST('1999-02-01' AS DATE) + INTERVAL 60 DAY)
    AND ws1.ws_ship_date_sk = d_date_sk
    AND ws1.ws_ship_addr_sk = ca_address_sk
    AND ca_state = 'IL'
    AND ws1.ws_web_site_sk = web_site_sk
    AND web_company_name = 'pri'
    AND ws1.ws_order_number IN (SELECT ws_order_number
  FROM ws_wh)
    AND ws1.ws_order_number IN (SELECT wr_order_number
  FROM web_returns, ws_wh
  WHERE wr_order_number = ws_wh.ws_order_number)
ORDER BY count(DISTINCT ws_order_number)
LIMIT 100
