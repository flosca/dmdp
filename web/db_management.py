# -*- coding: utf-8 -*-


class DBEntity:
    def __init__(self, filepath='./../parser/db/publications.db'):
        import sqlite3
        self.conn = sqlite3.connect(filepath)
        self.cursor = self.conn.cursor()


class Author(DBEntity):
    def __init__(self):
        super().__init__()

    def select_all(self):
        return self.cursor.execute('SELECT * FROM Author').fetchall()

    def select_by_id(self, author_id):
        return self.cursor.execute('SELECT * FROM Author WHERE Author_ID=?', [author_id]).fetchall()

    def select_by_keyname(self, keyname):
        return self.cursor.execute('SELECT * FROM Author WHERE Keyname LIKE ?', ['%' + keyname + '%']).fetchall()

    def select_by_forename(self, forename):
        return self.cursor.execute('SELECT * FROM Author WHERE Forenames LIKE ?' ['%' + forename + '%']).fetchall()

    def insert(self, keyname, forenames):
        result = self.cursor.execute('INSERT INTO Author (Keyname, Forenames) VALUES (?, ?)', [keyname, forenames])
        self.conn.commit()
        return result


class Include(DBEntity):
    def __init__(self):
        super().__init__()


class Publication(DBEntity):
    def __init__(self):
        super().__init__()


class Specification(DBEntity):
    def __init__(self):
        super().__init__()


class Write(DBEntity):
    def __init__(self):
        super().__init__()
