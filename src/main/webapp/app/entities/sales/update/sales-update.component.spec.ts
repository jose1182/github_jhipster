jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SalesService } from '../service/sales.service';
import { ISales, Sales } from '../sales.model';

import { SalesUpdateComponent } from './sales-update.component';

describe('Component Tests', () => {
  describe('Sales Management Update Component', () => {
    let comp: SalesUpdateComponent;
    let fixture: ComponentFixture<SalesUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let salesService: SalesService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SalesUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SalesUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SalesUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      salesService = TestBed.inject(SalesService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const sales: ISales = { id: 456 };

        activatedRoute.data = of({ sales });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(sales));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Sales>>();
        const sales = { id: 123 };
        jest.spyOn(salesService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ sales });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sales }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(salesService.update).toHaveBeenCalledWith(sales);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Sales>>();
        const sales = new Sales();
        jest.spyOn(salesService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ sales });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sales }));
        saveSubject.complete();

        // THEN
        expect(salesService.create).toHaveBeenCalledWith(sales);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Sales>>();
        const sales = { id: 123 };
        jest.spyOn(salesService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ sales });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(salesService.update).toHaveBeenCalledWith(sales);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
