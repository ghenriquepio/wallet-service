-- PERSON TABLE
CREATE TABLE person (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        full_name VARCHAR(255) NOT NULL,
                        document_number VARCHAR(50) NOT NULL UNIQUE,
                        email VARCHAR(255),
                        phone_number VARCHAR(20),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP
);

-- WALLET STATUS
CREATE TABLE wallet_status (
                               id SERIAL PRIMARY KEY,
                               name VARCHAR(50) NOT NULL UNIQUE,
                               description VARCHAR(255),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- WALLET TABLE
CREATE TABLE wallet (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        owner_id UUID NOT NULL,
                        balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,
                        status_id INTEGER NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_wallet_owner FOREIGN KEY (owner_id) REFERENCES person(id),
                        CONSTRAINT fk_wallet_status FOREIGN KEY (status_id) REFERENCES wallet_status(id)
);

-- WALLET HISTORY
CREATE TABLE wallet_history (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                wallet_id UUID NOT NULL,
                                status_before VARCHAR(50) NOT NULL,
                                status_after VARCHAR(50) NOT NULL,
                                changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                reason TEXT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TRANSACTION TABLE
CREATE TABLE transaction (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             wallet_id UUID NOT NULL,
                             type VARCHAR(20) NOT NULL, -- e.g., DEPOSIT, WITHDRAW
                             amount NUMERIC(15, 2) NOT NULL CHECK (amount >= 0),
                             description TEXT,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TRANSFER TABLE
CREATE TABLE transfer (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          wallet_id_from UUID NOT NULL,
                          wallet_id_to UUID NOT NULL,
                          amount NUMERIC(15, 2) NOT NULL CHECK (amount >= 0),
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);