from wallet.services.fund_wallet_service import paystack_callback
from django.urls import path

urlpatterns = [
    path('fund/', paystack_callback, name='fund_wallet'),
]
