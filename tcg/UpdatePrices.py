from traceback import format_tb
from playhouse.shortcuts import model_to_dict, dict_to_model
from peewee import SqliteDatabase
import requests
import json
from models.models import Sku, SkuPrice


db = SqliteDatabase('collection.db')
db.connect()


BASE_URL = "https://api.tcgplayer.com%s"
SKU_PRICE_URL = "/pricing/sku/SKU_ID"

headers = {"Accept": "application/json", "Authorization": "bearer a4ZIrg9dK5xYYaIbC0ZZQzj77MN_KIL1CcEAv9uV6mSv-h70dhUOPENB8vsjws_skYyjhuckyzBoarMifRiR3AYMXFXn-mIJVuqcRC740PhSkyf3VvLvdGBL0Ps8D5oz8cKQTicAMU2PQfIgkB8E47nAcuwiArCxeJ5Xsnofj7_HHglsoNsBRSBOSNeyWojpvVvAR33zVTdaPdR45tIlmlTgS4juA8iMdq4AGKZ2iU9KH5RORIrH1LRKS89UPYnbqob0h1RcBb-SMcPIj4aTxTdC6scLNGfEuTjVmn87CeRDVELNkDFyN3Tg2nG_p6VAmT9jRQ"}


for sku in SkuPrice.select():
    response = requests.get(BASE_URL.replace('%s',SKU_PRICE_URL.replace('SKU_ID', str(sku.skuId))), headers=headers, verify=False)
    price = response.json()['results']
    price.pop('lowestShipping', None)
    price.pop('lowestListingPrice', None)
    price.pop('directLowPrice', None)

    SkuPrice.update({SkuPrice.lowPrice:price['lowPrice']}).update({SkuPrice.marketPrice:price['marketPrice']}).where(SkuPrice.skuId==price['skuId'])
