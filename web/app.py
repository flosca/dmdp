import bottle
import beaker.middleware
from bottle import request, hook, get, post, view

__author__ = 'max'

session_opts = {
    'session.type': 'file',
    'session.data_dir': './session/',
    'session.cookie_expires': 300,
    'session.auto': True,
}


@hook('before_request')
def setup_request():
    request.session = request.environ['beaker.session']


def authenticated(func, redirect_url='/login'):
    def wrapped(*args, **kwargs):
        try:
            beaker_session = request.environ['beaker.session']
        except:
            bottle.abort(401, "Failed beaker_session in slash")

        try:
            auth = beaker_session['auth']

            if auth:
                return func(*args, **kwargs)
            else:
                bottle.redirect(redirect_url)
        except Exception as e:
            bottle.redirect(redirect_url)

    return wrapped


@bottle.get('/login')
@view('auth/login')
def login():
    return {}


@bottle.post('/login')
def submit_login():
    username = request.forms.get('username')
    password = request.forms.get('password')

    if username == password:
        request.session['auth'] = True
        return 'OK'

    return 'FAIL'


@bottle.get('/logout')
def logout():
    request.session['auth'] = None
    return 'OK'


@bottle.get('/hello')
@authenticated
def hello():
    return "<h1>Hello World!</h1>"


app = beaker.middleware.SessionMiddleware(bottle.app(), session_opts)

bottle.run(app=app, host='0.0.0.0', port=8080)
