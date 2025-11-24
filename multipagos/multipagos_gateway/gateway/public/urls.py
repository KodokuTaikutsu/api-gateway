from django.urls import path

from .views import (
    PublicCompaniesView,
    PublicCompanyDetailView,
    PublicServicesView,
    PublicServiceDetailView,
    PublicServiceFieldsView,
    DebtLookupView,
    PaymentQrGenerateProxyView,
    PaymentQrScanProxyView,
    PaymentQrGenerateFromDebtProxyView,
 
    
)

urlpatterns = [
    # Catalog
    path("catalog/companies/", PublicCompaniesView.as_view()),
    path("catalog/companies/<int:company_id>/", PublicCompanyDetailView.as_view()),
    path("catalog/services/", PublicServicesView.as_view()),
    path("catalog/services/<int:service_id>/", PublicServiceDetailView.as_view()),
    path(
        "catalog/services/<int:service_id>/fields/",
        PublicServiceFieldsView.as_view(),
    ),

    # Billing
    path("debts/lookup/", DebtLookupView.as_view()),

# --- NEW: payments / QR ---
    path(
        "payments/qr/generate/",
        PaymentQrGenerateProxyView.as_view(),
        name="payments-qr-generate",
    ),
    path(
        "payments/qr/scan/",
        PaymentQrScanProxyView.as_view(),
        name="payments-qr-scan",
    ),
    path(
        "payments/qr/generate-from-debt/",
        PaymentQrGenerateFromDebtProxyView.as_view(),
        name="payments-qr-generate-from-debt",
    ),
   
]
