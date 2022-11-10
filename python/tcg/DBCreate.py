from traceback import format_tb
from playhouse.shortcuts import model_to_dict, dict_to_model
from peewee import SqliteDatabase
import requests
import json

from models.models import Category, Condition, Group, Print, Product, Language, Sku, SkuPrice
requests.packages.urllib3.disable_warnings() 

db = SqliteDatabase('collection.db')
db.connect()
db.create_tables([Category,Condition,Group,Print,Product,Language,Sku,SkuPrice])


#URLs
BASE_URL = "https://api.tcgplayer.com%s"
CATEGORY_URL = "/catalog/categories?sortOrder=categoryId&offset=%s&limit=100"
GROUP_URL = "/catalog/categories/CATEGORY_ID/groups?offset=%s&limit=100"
PRODUCT_URL = "/catalog/products?groupId=GROUP_ID&productTypes=Cards&offset=%s&limit=100"
SKU_URL = "/catalog/products/PRODUCT_ID/skus"
SKU_PRICE_URL = "/pricing/sku/SKU_ID"
CONDITION_URL = "/catalog/categories/CATEGORY_ID/conditions"
PRINTING_URL = "/catalog/categories/CATEGORY_ID/printings"
LANGUAGE_URL = "/catalog/categories/CATEGORY_ID/languages"

#HEADERS
ACCEPT = "Accept"
ACCEPT_VALUE = "application/json"
AUTHORIZATION = "Authorization"
AUTHORIZATION_VALUE = "bearer %s"
bearerToken = "a4ZIrg9dK5xYYaIbC0ZZQzj77MN_KIL1CcEAv9uV6mSv-h70dhUOPENB8vsjws_skYyjhuckyzBoarMifRiR3AYMXFXn-mIJVuqcRC740PhSkyf3VvLvdGBL0Ps8D5oz8cKQTicAMU2PQfIgkB8E47nAcuwiArCxeJ5Xsnofj7_HHglsoNsBRSBOSNeyWojpvVvAR33zVTdaPdR45tIlmlTgS4juA8iMdq4AGKZ2iU9KH5RORIrH1LRKS89UPYnbqob0h1RcBb-SMcPIj4aTxTdC6scLNGfEuTjVmn87CeRDVELNkDFyN3Tg2nG_p6VAmT9jRQ"
headers = {"Accept": "application/json", "Authorization": "bearer a4ZIrg9dK5xYYaIbC0ZZQzj77MN_KIL1CcEAv9uV6mSv-h70dhUOPENB8vsjws_skYyjhuckyzBoarMifRiR3AYMXFXn-mIJVuqcRC740PhSkyf3VvLvdGBL0Ps8D5oz8cKQTicAMU2PQfIgkB8E47nAcuwiArCxeJ5Xsnofj7_HHglsoNsBRSBOSNeyWojpvVvAR33zVTdaPdR45tIlmlTgS4juA8iMdq4AGKZ2iU9KH5RORIrH1LRKS89UPYnbqob0h1RcBb-SMcPIj4aTxTdC6scLNGfEuTjVmn87CeRDVELNkDFyN3Tg2nG_p6VAmT9jRQ"}


def __pagedSearch(extension: str):
    offset = 0
    url = BASE_URL.replace('%s', extension)
    results = []
    while True:
        response = requests.get(url.replace('%s', str(offset)), headers=headers, verify=False)
        resp_res = response.json()['results']
        results.extend(resp_res)
        new_offset = offset + len(resp_res)

        if new_offset > offset:
            offset = new_offset
        else:
            break

    return results


# -------------MAIN--------------------------------------
data = __pagedSearch(CATEGORY_URL)
for element in data:
    element.pop('modifiedOn', None)
    element.pop('seoCategoryName', None)
    element.pop('sealedLabel', None)
    element.pop('nonSealedLabel', None)
    element.pop('conditionGuideUrl', None)
    element.pop('isScannable', None)
    element.pop('popularity', None)
    element.pop('isDirect', None)
    element['tracked'] = 0


Category.insert_many(data).execute()

print('\n\n\n---------------welcome---------------')
for cat in Category.select():
    user_input = input('would you like to track ' + str(cat.displayName) + '? (Y/N)')
    if user_input == 'Y' or user_input == 'y':
        qry = Category.update({Category.tracked: True}).where(Category.categoryId == cat.categoryId).execute()


print('\n Grabbing all sets for tracked Categories')
tracked_cats = Category.select().where(Category.tracked == True)

for cat in tracked_cats:
    print("grabbing groups for category: " + str(cat.name))
    # Grab all the groups for the tracked Categories
    groups = __pagedSearch(GROUP_URL.replace('CATEGORY_ID', str(cat.categoryId)))
    for element in groups:
        element.pop('isSupplemental', None)
        element.pop('modifiedOn', None)
    Group.insert_many(groups).execute()

    # Grab all the printings for each category
    print("grabbing printings for category: " + str(cat.name))
    prints = requests.get(BASE_URL.replace('%s',PRINTING_URL.replace('CATEGORY_ID', str(cat.categoryId))), headers=headers, verify=False).json()['results']
    for element in prints:
        element.pop('modifiedOn', None)
    Print.insert_many(prints).execute()

    # Grab all the conditions for each category
    print("grabbing conditions for category: " + str(cat.name))
    conditions = requests.get(BASE_URL.replace('%s',CONDITION_URL.replace('CATEGORY_ID', str(cat.categoryId))), headers=headers, verify=False).json()['results']
    Condition.insert_many(conditions).execute()

    # Grab all the languages for each category
    print("grabbing languages for category: " + str(cat.name))
    languages = requests.get(BASE_URL.replace('%s',LANGUAGE_URL.replace('CATEGORY_ID', str(cat.categoryId))), headers=headers, verify=False).json()['results']
    Language.insert_many(languages).execute()

# Grab all products
print("Grabbing all products...")
for group in Group.select():
    prods = __pagedSearch(PRODUCT_URL.replace('GROUP_ID', str(group.groupId)))

    for element in prods:
        element.pop('modifiedOn', None)
    Product.insert_many(prods).execute()

#SGrab all the Skus
print("Grabbing all skus...")
for prod in Product.select():

    response = requests.get(BASE_URL.replace('%s',SKU_URL.replace('PRODUCT_ID', str(prod.productId))), headers=headers, verify=False)
    skus = response.json()['results']
    print("product: " + str(prod.name) + " - skus: " + str(len(skus)) )
    for element in skus:
        element['count'] = 0
    Sku.insert_many(skus).execute()
    
# Grab all the prices for the skus
print("Grabbing all sku prices...")
for sku in Sku.select():
    response = requests.get(BASE_URL.replace('%s',SKU_PRICE_URL.replace('SKU_ID', str(sku.skuId))), headers=headers, verify=False)
    prices = response.json()['results']
    for element in prices:
        element.pop('lowestShipping', None)
        element.pop('lowestListingPrice', None)
        element.pop('directLowPrice', None)
    print("sku: " + str(sku.skuId) + " --- num of prices: " + str(len(prices)))
    SkuPrice.insert_many(prices).execute()

print("_____________________________ DONE _____________________________")