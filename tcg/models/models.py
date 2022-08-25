from dataclasses import dataclass
from peewee import SqliteDatabase
from peewee import Model, CharField, IntegerField, BooleanField, ForeignKeyField, PrimaryKeyField

DATA_BASE = SqliteDatabase('collection.db')

class Category(Model):
    categoryId = PrimaryKeyField()
    name = CharField()
    displayName = CharField()
    tracked = BooleanField(null=True)

    class Meta:
        database = DATA_BASE

class Condition(Model):
    conditionId = PrimaryKeyField()
    name = CharField()
    abbreviation = CharField(null=True)
    displayOrder = CharField()

    class Meta:
        database = DATA_BASE

class Group(Model):
    groupId = PrimaryKeyField()
    name = CharField()
    abbreviation = CharField(null=True)
    publishedOn = CharField()
    categoryId = ForeignKeyField(Category, backref='groups')
    
    class Meta:
        database = DATA_BASE

class Print(Model):
    printingId = PrimaryKeyField()
    name = CharField()
    displayOrder= IntegerField()

    class Meta:
        database = DATA_BASE

class Product(Model):
    productId = PrimaryKeyField()
    name = CharField()
    cleanName = CharField()
    imageUrl = CharField()
    categoryId = ForeignKeyField(Category, backref='products')
    groupId = ForeignKeyField(Group, backref='products')
    url = CharField()

    class Meta:
        database = DATA_BASE

class Language(Model):
    languageId = PrimaryKeyField()
    name = CharField()
    abbr = CharField()

    class Meta:
        database = DATA_BASE

class Sku(Model):
    skuId = PrimaryKeyField()
    productId = ForeignKeyField(Product,backref='skus')
    languageId = ForeignKeyField(Language, backref='skus')
    printingId = ForeignKeyField(Print, backref='skus')
    conditionId = ForeignKeyField(Condition, backref='skus')
    count =  IntegerField()

    class Meta:
        database = DATA_BASE

class SkuPrice(Model):
    skuId = ForeignKeyField(Sku, backref='skuPrices')
    lowPrice = IntegerField(null=True)
    marketPrice = IntegerField(null=True) 

    class Meta:
        database = DATA_BASE

