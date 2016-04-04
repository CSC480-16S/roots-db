/**
 * Profile of an Individual in the database.
 * id                      Unique identifier of the Individual
 * date_of_birth           The Indvidual's date of birth
 * municipality_of_birth   The municipality where the Indvidual was born
 * state_of_birth          The state where the Indvidual was born
 * country_of_birth        The country where the Indvidual was born
 * date_of_death           The date of the Indvidual's death, or NULL if they are still living
 * municipality_of_death   The municipality where the Indvidual died
 * state_of_death          The state where the Indvidual died
 * country_of_death        The country where the Indvidual died
 * gender                  The Indvidual's gender
 * bio                     Biographical notes on the Indvidual
 */
CREATE TABLE Individual(
   id INT NOT NULL AUTO_INCREMENT,
   date_of_birth DATE,
   municipality_of_birth VARCHAR(128),
   state_of_birth VARCHAR(64),
   country_of_birth VARCHAR(64),
   date_of_death DATE,
   municipality_of_death VARCHAR(128),
   state_of_death VARCHAR(64),
   country_of_death VARCHAR(64),
   gender VARCHAR(64),
   bio VARCHAR(5000),
   image INTEGER,
   PRIMARY KEY(id)
);

/**
 * A name associated with an Indivual.
 * id                   Unique identifier of the Name
 * individual_id        The Individual id of the person with the Name
 * first_name           The Individual's first Name
 * middle_name          The Individual's middle Name
 * last_name            The Individual's last Name
 * suffix               Suffix associated with the Name (e.g. Jr, II)
 * reason_for_change    Reason the name was changed (e.g. marriage)
 * date_from            Date the name was taken
 * date_to              Date the name was dropped
 */ 
CREATE TABLE Name(
   id INT NOT NULL AUTO_INCREMENT,
   individual_id INT NOT NULL,
   first_name VARCHAR(256) NOT NULL,
   middle_name VARCHAR(256),
   last_name VARCHAR(256) NOT NULL,
   suffix VARCHAR(256),
   reason_for_change VARCHAR(256),
   date_from DATE,
   date_to DATE,
   PRIMARY KEY(id),
   FOREIGN KEY(individual_id) REFERENCES Individual(id)
);

/**
 * Establishes that the Individual had an occupation at some point in time.
 * id                Unique identifier of the Occupation
 * individual_id     The Individual id of the person with the Occupation
 * occupation        Description of the Occupation
 * occupation_start  Start date of employment
 * occupation_end    End date of employment
 * employer          Employer
 * country           Country of employment
 * state             State of employment
 * Municipality      municipality of employment
 */
CREATE TABLE Occupation(
   id INT NOT NULL AUTO_INCREMENT,
   individual_id INT NOT NULL,
   occupation VARCHAR(256) NOT NULL,
   occupation_start DATE,
   occupation_end DATE,
   employer VARCHAR(256),
   country VARCHAR(256),
   state VARCHAR(256),
   municipality VARCHAR(256),
   PRIMARY KEY(id),
   FOREIGN KEY(individual_id) REFERENCES Individual(id)
);

/**
 * Establishes a parent/child relationship between two Individuals.
 * id          Unique identifier of the parent/child relationship
 * parent_id   The Individual id of the parent
 * child_id    The Individual id of the child
 */
CREATE TABLE Parent_of(
   id INT NOT NULL AUTO_INCREMENT,
   parent_id INT NOT NULL,
   child_id INT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(parent_id) REFERENCES Individual(id),
   FOREIGN KEY(child_id) REFERENCES Individual(id)
);

/**
 * Establishes a marriage relationship between two Individuals.
 * id                   Unique identifier of the marriage
 * spouse_1_id          The first spouse's individual id
 * spouse_2_id          The second spouse's individual id
 * marriage_date        The date of the marriage
 * marriage_end_date    Date the marriage ended
 * reason_for_end       Reason the marriage ended (e.g. death, divorce, annulment)
 */
CREATE TABLE Married_to(
   id INT NOT NULL AUTO_INCREMENT,
   spouse_1_id INT NOT NULL,
   spouse_2_id INT,
   marriage_date DATE NOT NULL,
   marriage_end_date DATE,
   reason_for_end VARCHAR(256),
   PRIMARY KEY(id),
   FOREIGN KEY(spouse_1_id) REFERENCES Individual(id),
   FOREIGN KEY(spouse_2_id) REFERENCES Individual(id)
);

/**
 * Profile of a user of the site for administrative purposes, distinct from a person's profile
 * within the genealogical data.
 * email             User's email address is their unique identifier
 * password          Hashed value of the user's password
 * password_reset    Password reset code
 * email_confirm     Email confirmation code. Once email is confirmed, this is NULL; a NULL value
 *                   indicates a confirmed email
 * individual_id     The id of the user's Individual profile in the genealogical database
 * login_count       Counter used to track attempts to log into an account within a cooldown period
 * timestamp         Timestamp of user's last login
 * cooldown          Login cooldown timestamp
 */
CREATE TABLE User(
   email VARCHAR(100) NOT NULL,
   password VARCHAR(32) NOT NULL,
   password_reset VARCHAR(32),
   email_confirm VARCHAR(32),
   individual_id INT,
   login_count INT,
   timestamp BIGINT,
   cooldown BIGINT,
   PRIMARY KEY(email),
   FOREIGN KEY(individual_id) REFERENCES Individual(id)
);
