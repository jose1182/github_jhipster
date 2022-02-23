import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISales, Sales } from '../sales.model';
import { SalesService } from '../service/sales.service';

@Component({
  selector: 'jhi-sales-update',
  templateUrl: './sales-update.component.html',
})
export class SalesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [],
  });

  constructor(protected salesService: SalesService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sales }) => {
      this.updateForm(sales);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sales = this.createFromForm();
    if (sales.id !== undefined) {
      this.subscribeToSaveResponse(this.salesService.update(sales));
    } else {
      this.subscribeToSaveResponse(this.salesService.create(sales));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISales>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(sales: ISales): void {
    this.editForm.patchValue({
      id: sales.id,
      title: sales.title,
    });
  }

  protected createFromForm(): ISales {
    return {
      ...new Sales(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
    };
  }
}
