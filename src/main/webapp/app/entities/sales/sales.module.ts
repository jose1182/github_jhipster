import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SalesComponent } from './list/sales.component';
import { SalesDetailComponent } from './detail/sales-detail.component';
import { SalesUpdateComponent } from './update/sales-update.component';
import { SalesDeleteDialogComponent } from './delete/sales-delete-dialog.component';
import { SalesRoutingModule } from './route/sales-routing.module';

@NgModule({
  imports: [SharedModule, SalesRoutingModule],
  declarations: [SalesComponent, SalesDetailComponent, SalesUpdateComponent, SalesDeleteDialogComponent],
  entryComponents: [SalesDeleteDialogComponent],
})
export class SalesModule {}
