from django.http import HttpResponse
from testproject.djangotest.models import *

# Create your views here.
def index( request ):
    ret = ""
    ret += "This is a test page<br>"
    for sim in TestSimulation.objects.all():
        ret += sim.name + "<br>"
    return HttpResponse( ret )
    