import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISales } from '../sales.model';
import { SalesService } from '../service/sales.service';

@Component({
  templateUrl: './sales-delete-dialog.component.html',
})
export class SalesDeleteDialogComponent {
  sales?: ISales;

  constructor(protected salesService: SalesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.salesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
