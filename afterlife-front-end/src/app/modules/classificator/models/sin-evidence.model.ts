export enum SinKind {
  //  Некрещенный
  UNBAPTIZED,
  //  Сладострастие
  VOLUPTUOUSNESS,
  //  Обжорство
  GLUTTONY,
  //  Расточительство
  WASTEFULNESS,
  //  Скупость
  AVARICE,
  //  Горделивость
  PRIDE,
  //  Еретичность
  HERETICNESS,
  //  Лжеучительство
  FALSE_TEACHING,
  //  Насилие
  VIOLENCE,
  //  Обман не доверившихся
  DECEPTION_WHO_NOT_TRUST,
  //  Обман доверившихся
  DECEPTION_WHO_TRUST,
}

export interface SinEvidence {
  id: number;
  kind: SinKind,
  dateOfSin: Date,
  attonedAt?: Date,
}
