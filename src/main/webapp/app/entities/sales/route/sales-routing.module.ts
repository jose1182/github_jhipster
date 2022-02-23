import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SalesComponent } from '../list/sales.component';
import { SalesDetailComponent } from '../detail/sales-detail.component';
import { SalesUpdateComponent } from '../update/sales-update.component';
import { SalesRoutingResolveService } from './sales-routing-resolve.service';

const salesRoute: Routes = [
  {
    path: '',
    component: SalesComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SalesDetailComponent,
    resolve: {
      sales: SalesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SalesUpdateComponent,
    resolve: {
      sales: SalesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SalesUpdateComponent,
    resolve: {
      sales: SalesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(salesRoute)],
  exports: [RouterModule],
})
export class SalesRoutingModule {}
