-- application.yml 파일의 defer-datasource-initialization: true 설정과 연관이 있음
-- hibernate가 초기화 되기 전에 data.sql이 실행이 되는데, 그러면 DDL이 수행되지 않았으므로 오류가 날 것이니
-- 해당 설정을 true로 변경하여 하이버네이트가 초기화 된 이후 수행 되도록 처리
insert into product(product_number, type, selling_status, name, price)
values ('001', 'HANDMADE', 'SELLING', '아메리카노', 4000),
       ('002', 'HANDMADE', 'HOLD', '카페라떼', 4500),
       ('003', 'BAKERY', 'STOP_SELLING', '크루아상', 3500);