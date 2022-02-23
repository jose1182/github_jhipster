import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'job',
        data: { pageTitle: 'Jobs' },
        loadChildren: () => import('./job/job.module').then(m => m.JobModule),
      },
      {
        path: 'sales',
        data: { pageTitle: 'Sales' },
        loadChildren: () => import('./sales/sales.module').then(m => m.SalesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
