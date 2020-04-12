CREATE TABLE
    assets_category
    (
        asset_type CHARACTER VARYING,
        id BIGSERIAL NOT NULL,
        CONSTRAINT assetscategory_asset_type UNIQUE (asset_type)
    );
CREATE TABLE
    assets_transaction
    (
        rate NUMERIC NOT NULL,
        transaction_type CHARACTER VARYING NOT NULL,
        transaction_date TIMESTAMP(6) WITH TIME ZONE NOT NULL,
        total_amount NUMERIC NOT NULL,
        quantity BIGINT NOT NULL,
        isin CHARACTER VARYING NOT NULL,
        portfolio_name CHARACTER VARYING NOT NULL,
        asset_type CHARACTER VARYING NOT NULL,
        scrip_name CHARACTER VARYING NOT NULL,
        id BIGSERIAL NOT NULL,
        CONSTRAINT assetstransaction_asset_type FOREIGN KEY (asset_type) REFERENCES
        "assets_category" ("asset_type"),
        CONSTRAINT assetstransaction_portfolio_name FOREIGN KEY (portfolio_name) REFERENCES
        "portfolio" ("name"),
        CONSTRAINT assetstransaction_trans_type CHECK ((transaction_type)::text = ANY (ARRAY[('BUY'
        ::CHARACTER VARYING)::text, ('SELL'::CHARACTER VARYING)::text]))
    );
CREATE TABLE
    current_holding
    (
        avg_rate NUMERIC NOT NULL,
        invested_amt NUMERIC NOT NULL,
        quantity NUMERIC NOT NULL,
        isin CHARACTER VARYING NOT NULL,
        portfolio_name CHARACTER VARYING NOT NULL,
        asset_type CHARACTER VARYING NOT NULL,
        scrip_name CHARACTER VARYING NOT NULL,
        id BIGSERIAL NOT NULL,
        CONSTRAINT assetstransaction_asset_type FOREIGN KEY (asset_type) REFERENCES
        "assets_category" ("asset_type"),
        CONSTRAINT assetstransaction_portfolio_name FOREIGN KEY (portfolio_name) REFERENCES
        "portfolio" ("name"),
        CONSTRAINT currentholding_unique_check UNIQUE (isin, portfolio_name, asset_type)
    );
CREATE TABLE
    nav
    (
        symbol CHARACTER VARYING(20),
        series CHARACTER VARYING(10),
        OPEN NUMERIC,
        high NUMERIC,
        low NUMERIC,
        CLOSE NUMERIC,
        last NUMERIC,
        prevclose NUMERIC,
        tottrdqty BIGINT,
        tottrdval NUMERIC,
        TIMESTAMP DATE,
        totaltrades BIGINT,
        isin CHARACTER VARYING(20),
        unnamed CHARACTER VARYING(10)
    );
CREATE TABLE
    navhistory
    (
        symbol CHARACTER VARYING(20) NOT NULL,
        series CHARACTER VARYING(10) NOT NULL,
        OPEN NUMERIC NOT NULL,
        high NUMERIC,
        low NUMERIC,
        CLOSE NUMERIC NOT NULL,
        last NUMERIC,
        prevclose NUMERIC,
        tottrdqty BIGINT,
        tottrdval NUMERIC,
        TIMESTAMP DATE NOT NULL,
        totaltrades BIGINT,
        isin CHARACTER VARYING(20),
        unnamed CHARACTER VARYING(10),
        id BIGSERIAL NOT NULL,
        PRIMARY KEY (id)
    );
CREATE TABLE
    portfolio
    (
        id BIGSERIAL NOT NULL,
        name CHARACTER VARYING NOT NULL,
        UNIQUE (name)
    );
