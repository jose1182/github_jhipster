export interface IJob {
  id?: number;
  title?: string;
  description?: string | null;
}

export class Job implements IJob {
  constructor(public id?: number, public title?: string, public description?: string | null) {}
}

export function getJobIdentifier(job: IJob): number | undefined {
  return job.id;
}
