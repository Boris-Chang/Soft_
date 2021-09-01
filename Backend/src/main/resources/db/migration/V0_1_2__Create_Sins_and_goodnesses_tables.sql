CREATE TYPE Sin_Kind as ENUM (
    --  Некрещенный
    'UNBAPTIZED',
    --  Сладострастие
    'VOLUPTUOUSNESS',
    --  Обжорство
    'GLUTTONY',
    --  Расточительство
    'WASTEFULNESS',
    --  Скупость
    'AVARICE',
    --  Горделивость
    'PRIDE',
    --  Еретичность
    'HERETICNESS',
    --  Лжеучительство
    'FALSE_TEACHING',
    --  Насилие
    'VIOLENCE',
    --  Обман не доверившихся
    'DECEPTION_WHO_NOT_TRUST',
    --  Обман доверившихся
    'DECEPTION_WHO_TRUST'
);
CREATE TYPE Goodness_Kind AS ENUM (
    --  Нарушения обета по чужой вине
    'BREAKING_VOW_BY_ELSE',
    --  Реформаторство
    'REFORMISM',
    --  Честолюбивая деятельность
    'AMBITION',
    --  Влюбленность
    'LOVE',
    --  Мудрость
    'WISDOM',
    --  Ученность
    'SCHOLARSHIP',
    --  Война за веру
    'WAR_FOR_FAITH',
    --  Справедливое правление
    'FAIR_GOVERNMENT',
    --  Богословие
    'THEOLOGY',
    --  Монашество
    'MONASTICISM',
    --  Торжествование
    'TRIUMPH',
    --  Святость
    'HOLINESS',
    --  Божественность, блаженность
    'DIVINITY'
);

CREATE TABLE Sin_Evidences (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Kind Sin_Kind NOT NULL,
    Date_Of_Sin TIMESTAMP NOT NULL,
    Attoned_At TIMESTAMP,
    Sinned_By_Soul_Id BIGINT NOT NULL REFERENCES Souls(id)
);

CREATE TABLE Goodness_Evidences (
    Id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Kind Goodness_Kind NOT NULL,
    Date_Of_Good_Deed_Evidence TIMESTAMP NOT NULL,
    Done_By_Soul_Id BIGINT REFERENCES Souls(ID) NOT NULL
);