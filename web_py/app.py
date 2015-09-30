import sys
import traceback

import beaker.middleware
import bottle
from bottle import request, hook, view

from web.db_management import Author

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
            auth = beaker_session['auth'] if 'auth' in beaker_session else None

            if auth:
                return func(*args, **kwargs)
            else:
                bottle.redirect(redirect_url)
        except Exception as e:
            print('ERROR')
            traceback.print_exc(file=sys.stdout)
            bottle.redirect(redirect_url)

    return wrapped


class AuthRoutes:
    @staticmethod
    @bottle.get('/login')
    @view('auth/login')
    def login():
        return {}

    @staticmethod
    @bottle.post('/login')
    def submit_login():
        username = request.forms.get('username')
        password = request.forms.get('password')

        if username == password:
            request.session['auth'] = True
            return 'OK'

        return 'FAIL'

    @staticmethod
    @bottle.get('/logout')
    def logout():
        request.session['auth'] = None
        return 'OK'


class AuthorRoutes:
    @staticmethod
    @bottle.get('/author/show')
    @authenticated
    def author_show_all():
        a = Author()
        author_rows = a.select_all()
        return '%s' % author_rows

    @staticmethod
    @bottle.get('/author/show/keyname/:keyname')
    @authenticated
    def author_show_by_keyname(keyname):
        a = Author()
        author_rows = a.select_by_keyname(keyname)
        return '%s' % author_rows

    @staticmethod
    @bottle.get('/author/show/forenames/:forename')
    @authenticated
    def author_show_by_forename(forename):
        a = Author()
        author_rows = a.select_by_forename(forename)
        return '%s' % author_rows

    @staticmethod
    @bottle.get('/author/create')
    @view('author/create')
    @authenticated
    def author_create():
        return {}

    @staticmethod
    @bottle.post('/author/create')
    @view('author/create')
    @authenticated
    def submit_author_create():
        keyname = request.forms.get('keyname')
        forenames = request.forms.get('forenames')

        if forenames in ('', None) or keyname in ('', None):
            return 'FAIL'

        a = Author()
        result = a.insert(keyname, forenames)
        return 'OK %s' % result


bottle_app = bottle.app()
app = beaker.middleware.SessionMiddleware(bottle_app, session_opts)

bottle.run(app=app, host='0.0.0.0', port=8080)
