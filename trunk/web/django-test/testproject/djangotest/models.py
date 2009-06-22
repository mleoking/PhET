from django.db import models

# Create your models here.
class TestSimulation(models.Model):
    type = models.IntegerField()
    project = models.CharField(max_length=200)
    simulation = models.CharField(max_length=200)
    name = models.CharField(max_length=200)
    sorting_name = models.CharField(max_length=200)

class TestTranslation(models.Model):
    simulation = models.ForeignKey(TestSimulation)
    language = models.CharField(max_length=2)
    country = models.CharField(max_length=2)