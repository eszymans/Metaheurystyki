import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import os

# --- KONFIGURACJA ---
# Ustaw parametry Twojego "Wariantu 5" do filtrowania danych
WARIANT_N = 200  # Wielkość populacji
WARIANT_T = 1000  # Liczba iteracji

# Ustawienie stylu wykresów
sns.set_theme(style="whitegrid")
plt.rcParams['figure.figsize'] = (10, 6)
plt.rcParams['font.size'] = 11


def wczytaj_dane():
    # Sprawdzenie czy pliki istnieją
    if not os.path.exists('results.csv'):
        print("BŁĄD: Nie znaleziono pliku results.csv! Uruchom najpierw program w Javie (Opcja 1).")
        return None, None

    # Wczytanie wyników ogólnych
    # UWAGA: Jeśli masz polskiego Excela i liczby z przecinkami, dodaj argument: decimal=','
    df = pd.read_csv('results.csv', sep=';')

    # Wczytanie przebiegu (opcjonalne, bo może go nie być)
    df_iter = None
    if os.path.exists('przebieg_iteracji.csv'):
        df_iter = pd.read_csv('przebieg_iteracji.csv', sep=';')
    else:
        print("UWAGA: Nie znaleziono przebieg_iteracji.csv. Wykres zbieżności zostanie pominięty.")

    return df, df_iter


def generuj_tabele(df):
    print("\n" + "=" * 50)
    print("GENEROWANIE TABEL DO SPRAWOZDANIA")
    print("=" * 50)

    # Funkcja pomocnicza do ładnego wyświetlania
    def pokaz_statystyki(grupa, nazwa_parametru):
        stats = df.groupby(grupa)['bestFitness'].agg(['mean', 'median', 'min', 'max', 'std']).round(2)
        stats.columns = ['Średnia', 'Mediana', 'Min', 'Max', 'Odch. Std.']
        print(f"\n--- Wpływ parametru: {nazwa_parametru} ---")
        print(stats.to_string())
        # Zapis do CSV dla łatwego kopiowania do Excela/Worda
        stats.to_csv(f'tabela_statystyki_{nazwa_parametru}.csv', sep=';')

    # 1. Wpływ Pc
    pokaz_statystyki('Pc', 'Prawdopodobieństwo Krzyżowania (Pc)')

    # 2. Wpływ Pm
    pokaz_statystyki('Pm', 'Prawdopodobieństwo Mutacji (Pm)')

    # 3. Wpływ N (Populacji)
    pokaz_statystyki('N', 'Rozmiar Populacji (N)')

    # 4. Porównanie Metod (Dla wariantu 5)
    print(f"\n--- Porównanie Metod (Dla N={WARIANT_N}, T={WARIANT_T}) ---")
    maska = (df['N'] == WARIANT_N) & (df['T'] == WARIANT_T)
    df_var5 = df[maska]

    if not df_var5.empty:
        stats_methods = df_var5.groupby(['Selection', 'Crossover'])['bestFitness'].agg(['mean', 'max', 'std']).round(2)
        stats_methods.columns = ['Średnia', 'Max', 'Odch. Std.']
        print(stats_methods.to_string())
        stats_methods.to_csv('tabela_statystyki_metody.csv', sep=';')
    else:
        print("Brak danych dla podanego Wariantu 5 (sprawdź N i T w konfiguracji skryptu).")


def generuj_wykresy(df, df_iter):
    print("\n" + "=" * 50)
    print("GENEROWANIE WYKRESÓW")
    print("=" * 50)

    # --- WYKRES 1: Zbieżność (Ewolucja w czasie) ---
    if df_iter is not None:
        plt.figure()
        plt.plot(df_iter['Iteracja'], df_iter['Najlepsza'], label='Najlepszy osobnik', color='red', linewidth=2)
        plt.plot(df_iter['Iteracja'], df_iter['Srednia'], label='Średnia populacji', color='blue', linestyle='--',
                 alpha=0.7)
        plt.title('Przebieg ewolucji (Zbieżność algorytmu)')
        plt.xlabel('Numer pokolenia (Iteracja)')
        plt.ylabel('Wartość funkcji przystosowania')
        plt.legend()
        plt.grid(True, alpha=0.3)
        plt.tight_layout()
        plt.savefig('Wykres_1_Zbieznosc.png', dpi=300)
        print("Zapisano: Wykres_1_Zbieznosc.png")

    # --- WYKRES 2: Wpływ Pc (Liniowy) ---
    plt.figure()
    # Agregujemy dane, żeby wykres był czytelny (średnia dla każdego Pc)
    sns.lineplot(data=df, x='Pc', y='bestFitness', marker='o', linewidth=2.5, errorbar=('ci', 95))
    plt.title('Wpływ prawdopodobieństwa krzyżowania (Pc) na wynik')
    plt.xlabel('Prawdopodobieństwo Krzyżowania (Pc)')
    plt.ylabel('Średnia wartość najlepszego rozwiązania')
    plt.tight_layout()
    plt.savefig('Wykres_2_Wplyw_Pc.png', dpi=300)
    print("Zapisano: Wykres_2_Wplyw_Pc.png")

    # --- WYKRES 3: Wpływ Pm (Liniowy) ---
    plt.figure()
    sns.lineplot(data=df, x='Pm', y='bestFitness', marker='o', color='green', linewidth=2.5)
    plt.title('Wpływ prawdopodobieństwa mutacji (Pm) na wynik')
    plt.xlabel('Prawdopodobieństwo Mutacji (Pm)')
    plt.ylabel('Średnia wartość najlepszego rozwiązania')
    plt.tight_layout()
    plt.savefig('Wykres_3_Wplyw_Pm.png', dpi=300)
    print("Zapisano: Wykres_3_Wplyw_Pm.png")

    # --- WYKRES 4: Porównanie Metod (Słupkowy - Wariant 5) ---
    maska = (df['N'] == WARIANT_N) & (df['T'] == WARIANT_T)
    df_var5 = df[maska]

    if not df_var5.empty:
        plt.figure()
        ax = sns.barplot(data=df_var5, x='Selection', y='bestFitness', hue='Crossover', palette='viridis',
                         errorbar=None)

        # ZOOM na oś Y (żeby było widać różnice)
        min_val = df_var5['bestFitness'].min() * 0.98
        max_val = df_var5['bestFitness'].max() * 1.01
        plt.ylim(min_val, max_val)

        plt.title(f'Porównanie metod selekcji i krzyżowania (N={WARIANT_N}, T={WARIANT_T})')
        plt.ylabel('Średnia wartość rozwiązania')
        plt.xlabel('Metoda Selekcji')
        plt.legend(title='Krzyżowanie')
        plt.tight_layout()
        plt.savefig('Wykres_4_Porownanie_Metod.png', dpi=300)
        print("Zapisano: Wykres_4_Porownanie_Metod.png")

    # --- WYKRES 5: Najlepsze vs Średnie vs Najgorsze (Boxplot lub Barplot) ---
    # Pokażemy rozkład wyników dla różnych wielkości populacji
    plt.figure()
    # Przekształcenie danych do formatu "długiego" (melt) aby narysować 3 słupki obok siebie
    df_melted = df.melt(id_vars=['N'], value_vars=['bestFitness', 'AvgFitness', 'worstFitness'],
                        var_name='Typ_Wyniku', value_name='Wartosc')

    # Zmiana nazw dla legendy
    df_melted['Typ_Wyniku'] = df_melted['Typ_Wyniku'].map({
        'bestFitness': 'Najlepszy',
        'AvgFitness': 'Średni (z 5 prób)',
        'worstFitness': 'Najgorszy'
    })

    sns.barplot(data=df_melted, x='N', y='Wartosc', hue='Typ_Wyniku', palette='rocket')
    plt.title('Zestawienie wyników (Best/Avg/Worst) względem populacji')
    plt.ylim(df['worstFitness'].min() * 0.9, df['bestFitness'].max() * 1.05)
    plt.xlabel('Rozmiar Populacji (N)')
    plt.ylabel('Wartość funkcji przystosowania')
    plt.tight_layout()
    plt.savefig('Wykres_5_Best_Avg_Worst.png', dpi=300)
    print("Zapisano: Wykres_5_Best_Avg_Worst.png")


# --- URUCHOMIENIE ---
if __name__ == "__main__":
    df, df_iter = wczytaj_dane()
    if df is not None:
        generuj_tabele(df)
        generuj_wykresy(df, df_iter)
        print("\nGOTOWE! Wykresy i tabele CSV zostały zapisane w folderze.")