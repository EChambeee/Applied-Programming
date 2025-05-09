
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_csv('allRatingsFinal.csv')

df_latest = df[df['week'] == 'SUPER BOWL RATINGS']

position_ratings = df_latest.groupby('position')['ovr'].mean().sort_values(ascending=False)

plt.figure(figsize=(12, 6))
sns.barplot(x=position_ratings.values, y=position_ratings.index, palette='coolwarm')
plt.title('Average Overall Rating by Position (Super Bowl Week)')
plt.xlabel('Average Overall Rating')
plt.ylabel('Position')
plt.tight_layout()
plt.show()

if 'age' in df.columns:
    young_players = df[(df['age'] <= 25) & (df['week'] == 'SUPER BOWL RATINGS')]
    average_speed = young_players['spd'].mean()
    print(f"Average speed rating of players aged 25 or younger (Super Bowl Week): {average_speed:.2f}")
else:
    print("Age data not available in this dataset. Skipping Question 2.")
