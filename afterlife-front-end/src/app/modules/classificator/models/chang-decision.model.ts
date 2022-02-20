export interface ChangeDecision {
    godChangedDecision: {
        circleNumber: number,
        afterwoldKind: string,
        sectionIndex: number
    },
    devilChangedDecision: {
        circleNumber: number,
        afterwoldKind: string,
        sectionIndex: number
    },
    isArguedByGod: boolean,
    isArguedByDevil: boolean,
    decisionText: string;
}