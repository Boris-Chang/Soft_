import { DevilChangedDecision } from './devilChangedDecision.model';
import { godChangedDecision } from './godChangedDecision.model';

export interface Argue{
    godChangedDecision: godChangedDecision;
    devilChangedDecision: DevilChangedDecision;
    isArguedByGod: boolean;
    isArguedByDevil: boolean;
    decisionText: string;
}
