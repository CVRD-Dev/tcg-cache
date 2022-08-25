import typer
import DBCreate
import UpdatePrices
app = typer.Typer()

@app.command()
def createDatabase():
    DBCreate()

@app.command()
def updatePrices():
    UpdatePrices
