create table nota_de_negociacao (
    id bigint unsigned not null primary key auto_increment,
    data_pregao date not null,
    titulo varchar(45) not null,
    qty int not null,
    price_amount decimal(15,2) not null,
    total_amount decimal(15,2) not null,
    created_at timestamp not null,
    index nota_de_negociacao_titulo_index (titulo)
)