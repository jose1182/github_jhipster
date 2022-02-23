export interface ISales {
  id?: number;
  title?: string | null;
}

export class Sales implements ISales {
  constructor(public id?: number, public title?: string | null) {}
}

export function getSalesIdentifier(sales: ISales): number | undefined {
  return sales.id;
}
