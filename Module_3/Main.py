import sqlite3
from datetime import datetime

conn = sqlite3.connect('movies.db')
cursor = conn.cursor()

cursor.execute('''
CREATE TABLE IF NOT EXISTS movies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    genre TEXT,
    rating INTEGER CHECK (rating BETWEEN 1 AND 10),
    watched_on DATE
)
''')
conn.commit()

def add_movie():
    title = input("Enter movie title: ")
    genre = input("Enter genre: ")
    rating = int(input("Enter rating (1-10): "))
    watched_on = input("Enter date watched (MM-DD-YYYY): ")
    try
        date_obj = datetime.strptime(watched_on, '%m-%d-%Y')
        formatted_date = date_obj.strftime('%Y-%m-%d')
        cursor.execute('''
        INSERT INTO movies (title, genre, rating, watched_on)
        VALUES (?, ?, ?, ?)
        ''', (title, genre, rating, formatted_date))
        conn.commit()
        print("Movie added successfully.\n")
    except ValueError:
        print("Invalid date format. Please use MM-DD-YYYY.\n")

def list_movies():
    cursor.execute('SELECT * FROM movies')
    rows = cursor.fetchall()
    if rows:
        print("\nAll Movies:")
        for row in rows:
            print(row)
    else:
        print("\nNo movies found.")
    print()

def update_rating():
    movie_id = int(input("Enter movie ID to update: "))
    new_rating = int(input("Enter new rating (1-10): "))
    cursor.execute('UPDATE movies SET rating = ? WHERE id = ?', (new_rating, movie_id))
    conn.commit()
    print("Rating updated.\n")

def delete_movie():
    movie_id = int(input("Enter movie ID to delete: "))
    cursor.execute('DELETE FROM movies WHERE id = ?', (movie_id,))
    conn.commit()
    print("Movie deleted.\n")

def show_aggregates():
    cursor.execute('SELECT COUNT(*) FROM movies')
    total = cursor.fetchone()[0]
    cursor.execute('SELECT AVG(rating) FROM movies')
    average = cursor.fetchone()[0]
    print(f"\nTotal movies: {total}")
    print(f"Average rating: {average:.2f}" if average else "Average rating: N/A")
    print()

def main():
    while True:
        print("Movie Rating Database")
        print("1. Add movie")
        print("2. List all movies")
        print("3. Update rating")
        print("4. Delete movie")
        print("5. Show rating summary (aggregate functions)")
        print("6. Exit")

        choice = input("Select an option (1-6): ")

        if choice == '1':
            add_movie()
        elif choice == '2':
            list_movies()
        elif choice == '3':
            update_rating()
        elif choice == '4':
            delete_movie()
        elif choice == '5':
            show_aggregates()
        elif choice == '6':
            break
        else:
            print("Invalid option. Please try again.\n")

    conn.close()

if __name__ == '__main__':
    main()
