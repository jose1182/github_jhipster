import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SalesDetailComponent } from './sales-detail.component';

describe('Component Tests', () => {
  describe('Sales Management Detail Component', () => {
    let comp: SalesDetailComponent;
    let fixture: ComponentFixture<SalesDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SalesDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ sales: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SalesDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SalesDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load sales on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.sales).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
