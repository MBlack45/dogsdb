CREATE DATABASE dogsdb;

USE dogsdb;

CREATE TABLE IF NOT EXISTS `championship`
(
    `id`      INT NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(50) DEFAULT NULL,
    `date`    DATE        DEFAULT NULL,
    `address` VARCHAR(50) DEFAULT NULL,
    `prizes`  VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

INSERT INTO championship(`id`, `name`, `date`, `address`, `prizes`)
VALUES (1, 'Чемп обл', '2023-04-19', 'М.Харків, Вул Академіка Вальтера, буд. 9', 'перше місце'),
       (2, 'Чемпіонат України', '2021-03-20', 'М. Київ, Вул. Січових Стрільців буд. 38', NULL),
       (3, 'Всеєвропейська виставка такс', '2014-02-20', 'М. Відень, Вулиця Ландштрассе 15Б', NULL),
       (4, '\"Хаскі харківщини\"', '2019-05-20', 'М. Чугуїв , Вул. Арсенальна 199', NULL),
       (5, '\"Болонки слобожанщини\"', '2001-05-20', 'М. Красноград, Вул Харківська 19', NULL);
DROP TABLE IF EXISTS `dog`;

CREATE TABLE `dog`
(
    `id`          INT NOT NULL AUTO_INCREMENT,
    `breed`       VARCHAR(50) DEFAULT NULL,
    `description` VARCHAR(50)                                           DEFAULT NULL,
    `imgURL`      VARCHAR(50)                                           DEFAULT NULL,
    `age`         INT                                                   DEFAULT NULL,
    `name`        VARCHAR(50)                                           DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `dog`(`id`, `breed`, `description`, `imgURL`, `age`, `name`)
VALUES (1, 'Husky', 'Елегантна , витончена жінка', 'http', 1, 'Valera'),
       (2, 'dachshund', 'Полюбляє полювання, нори та лисиць', 'лпла', 2, 'Ленні'),
       (3, 'Husky', 'Володар зимової тундри та землі на подвір\'ї', 'оллв', 3, 'Персиваль'),
       (4, 'basset hound', 'Собака добрий', 'віпр', 4, 'Висялер');


DROP TABLE IF EXISTS `championshipparticipant`;

CREATE TABLE `championshipparticipant`
(
    `champid` INT NOT NULL,
    `dogid`   INT DEFAULT NULL,
    PRIMARY KEY (`champid`),
    KEY `dogid` (`dogid`),
    CONSTRAINT `championshipparticipant_ibfk_1` FOREIGN KEY (`champid`) REFERENCES `championship` (`id`),
    CONSTRAINT `championshipparticipant_ibfk_2` FOREIGN KEY (`dogid`) REFERENCES `dog` (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;



INSERT INTO `championshipparticipant`(`champid`, `dogid`)
VALUES (1, 2);

DROP TABLE IF EXISTS `doctor`;

CREATE TABLE `doctor`
(
    `id`            INT NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(50)                                                              DEFAULT NULL,
    `phone`         VARCHAR(50)                                                              DEFAULT NULL,
    `qualification` ENUM ('Терапевт','Хірург','Дерматолог','Невропатолог','Гастроінтеролог') DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


INSERT INTO `doctor`(`id`, `name`, `phone`, `qualification`)
VALUES (1, 'Чередниченко Артем Олегович', '+380687647773', 'Хірург'),
       (2, 'Петренко Іван Анастасович', '+380600694688', 'Невропатолог'),
       (3, 'Ніколайчук Артем Сергійович', '+380621324532', 'Хірург');

DROP TABLE IF EXISTS `illnesslist`;

CREATE TABLE `illnesslist`
(
    `id`             INT NOT NULL AUTO_INCREMENT,
    `dogId`          INT                                                                           DEFAULT NULL,
    `doctorid`       INT                                                                           DEFAULT NULL,
    `description`    VARCHAR(50)                                                                   DEFAULT NULL,
    `recomendations` VARCHAR(50)                                                                   DEFAULT NULL,
    `status`         ENUM ('Хворіє','Вилікувана') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `date`           DATE                                                                          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`),
    KEY `dogId` (`dogId`),
    KEY `doctorid` (`doctorid`),
    CONSTRAINT `illnesslist_ibfk_2` FOREIGN KEY (`doctorid`) REFERENCES `doctor` (`id`),
    CONSTRAINT `illnesslist_ibfk_3` FOREIGN KEY (`dogId`) REFERENCES `dog` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


INSERT INTO `illnesslist`(`id`, `dogId`, `doctorid`, `description`, `recomendations`, `status`, `date`)
VALUES (2, 2, 2, 'Haries', 'Deleting tooth', 'Вилікувана', '2023-05-04');

DROP TABLE IF EXISTS `owner`;

CREATE TABLE `owner`
(
    `id`      INT NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(50) DEFAULT NULL,
    `phone`   VARCHAR(50) DEFAULT NULL,
    `address` VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `owner`(`id`, `name`, `phone`, `address`)
VALUES (1, 'Чорний Максим Андрійович', '+380987639873', 'М. Кременчук'),
       (2, 'Чередниченко Артем Вадимович', '+380687647773', 'М. Інгулець'),
       (3, 'Конюшенко Поліна Вікторівна', '+380594993295', 'М. Полтава'),
       (4, 'Ніколайчук Артем Сергійович', '+380688582586', 'М. Кривий Ріг'),
       (5, 'Дубинка Анастасія', '+3809821432', 'М. Харків');

DROP TABLE IF EXISTS `themes`;

CREATE TABLE `themes`
(
    `id`      INT NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(50) DEFAULT NULL,
    `ownerid` INT         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `ownerid` (`ownerid`),
    CONSTRAINT `themes_ibfk_1` FOREIGN KEY (`ownerid`) REFERENCES `owner` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `themes`(`id`, `name`, `ownerid`)
VALUES (1, 'Блохи у мальтійських болонок', 2),
       (2, 'Новий штамм гепатиту у вівчарок', 5);

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message`
(
    `id`      INT NOT NULL AUTO_INCREMENT,
    `text`    VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `ownerid` INT                                                           DEFAULT NULL,
    `themeid` INT                                                           DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `id` (`id`),
    KEY `themeid` (`themeid`),
    KEY `ownerid` (`ownerid`),
    CONSTRAINT `message_ibfk_1` FOREIGN KEY (`ownerid`) REFERENCES `owner` (`id`),
    CONSTRAINT `message_ibfk_2` FOREIGN KEY (`themeid`) REFERENCES `themes` (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


INSERT INTO `message`(`id`, `text`, `ownerid`, `themeid`)
VALUES (2, 'Вельми дивно ти себе поводиш.Не думав купити ТІ самі спрей-таблетки від мальтійських тигрових бліх?', 1, 1),
       (1,
        'Вже певний час веду безупинну, але безрезультатну боротбу з мешканцями моєї кудлатої філі.Чи маєте якісь ідеї?',
        2, 1),
       (3, 'А з цим вже сам вирішуй, у моєї коротке хутро хД', 3, 1),
       (4, 'Купи собі вівчарку,крінжулік. З цими \"вишуканими породами\" більше мороки ніж профіту', 4, 1),
       (5,
        'Ось така нелегка хвороба сталась з моєю любою вівцепаскою, чи мав хтось щось подібне,як лікувати в  Україні????',
        5, 2);

DROP TABLE IF EXISTS `ownerdog`;

CREATE TABLE `ownerdog`
(
    `dogid`   INT NOT NULL,
    `ownerid` INT NOT NULL,
    PRIMARY KEY (`dogid`),
    KEY `ownerid` (`ownerid`),
    CONSTRAINT `ownerdog_ibfk_1` FOREIGN KEY (`dogid`) REFERENCES `dog` (`id`),
    CONSTRAINT `ownerdog_ibfk_2` FOREIGN KEY (`ownerid`) REFERENCES `owner` (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `ownerdog`(`dogid`, `ownerid`)
VALUES (1, 2),
       (2, 5);