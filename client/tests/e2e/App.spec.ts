// Testing class
import { expect, test } from "@playwright/test";
import { setupClerkTestingToken, clerk, clerkSetup } from "@clerk/testing/playwright";
import dotenv from "dotenv";
dotenv.config();

const email = process.env.E2E_CLERK_USER_USERNAME;
if (!email) throw new Error("E2E_CLERK_USER_USERNAME is not set");
const pwd = process.env.E2E_CLERK_USER_PASSWORD;
if (!pwd) throw new Error("E2E_CLERK_USER_PASSWORD is not set");

/**
 * Intializes connection to local host and logs into an exisitng account before each test.
 */

test.beforeEach('testing defaults before each', async ({ page }) => {
  await clerkSetup({
    frontendApiUrl: process.env.CLERK_FRONTEND_API,
  });

  if (!process.env.CLERK_FRONTEND_API) {
    throw new Error(
      "CLERK_FRONTEND_API is not set in environment variables"
    )
  }

  await setupClerkTestingToken({ page })
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign In' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill(email);
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill(pwd);
  await page.getByRole('button', { name: 'Continue' }).click();
  
});

/**
 * Tests that home page components are visible as expected upon login
 */

test('test home page componenets', async ({ page }) => {
    await expect(page.getByPlaceholder('Search events...')).toBeVisible();
    await expect(page.locator('.card-grid')).toBeVisible();
    await expect(page.getByRole('button', { name: 'See Friends' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Recommend' }).nth(1)).toBeVisible();
    await expect(page.getByLabel('Open user button').nth(1)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Profile' }).nth(1)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Sign Out' }).nth(1)).toBeVisible();
    await expect(page.getByRole('button', { name: '+' }).nth(1)).toBeVisible(); 
});

/**
 * Tests that profile components are visible as expected upon login and click on profiile page.
 */

test('test profile components', async ({ page }) => {
    // checking profile page has expected (generic) elements
    await page.getByRole('button', { name: 'Profile' }).nth(1).click();
    await expect(page.getByRole('heading', { name: 'Connections' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'My Friends' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'My Interests' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'My Favorite CanGo Organizers' })).toBeVisible();
    await expect(page.locator('#add-tag')).toBeVisible(); // should be visible for own profile
    await expect(page.locator('#add-org')).toBeVisible(); // should be visible for own profile
    await expect(page.getByRole('heading', { name: 'Event History' })).toBeVisible();
    await expect(page.getByRole('img', { name: 'Profile' })).toBeVisible();

    // checking that specific componenets exist (only for this particular profile)
    await expect(page.getByRole('button', { name: 'Taylor Swift', exact: true })).toBeVisible();
    await expect(page.getByRole('button', { name: 'TS' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Film Club' })).toBeVisible();

})

/**
 * Tests that recommend feature works for this particular profile (Taylor Swift events show up).
 */

test('test recommend', async ({ page }) => {
    await expect(page.getByRole('button', { name: 'Recommend' }).nth(1)).toBeVisible();
    await page.getByRole('button', { name: 'Recommend' }).nth(1).click();
    await expect(page.getByText('TaylorokeDate: 2025-05-15')).toBeVisible();
})

/**
 * Tests that event creation, editing, and deletion works as expected.
 */
test('test event creation/editing/deletion', async ({ page }) => {
    // event creation
    await page.getByRole('button', { name: '+' }).nth(1).click();

    // checking relevant fields are visible
    await expect(page.getByRole('heading', { name: 'New Event' })).toBeVisible();
    await expect(page.getByPlaceholder('Your Event Name Here...')).toBeVisible();
    await expect(page.getByText('Date', { exact: true })).toBeVisible();
    await expect(page.locator('input[name="date"]')).toBeVisible();
    await expect(page.getByText('Time', { exact: true })).toBeVisible();
    await expect(page.locator('input[type="time"]').first()).toBeVisible();
    await expect(page.locator('input[type="time"]').nth(1)).toBeVisible();
    await expect(page.getByText('Organizer')).toBeVisible();
    await expect(page.getByText('Thumbnail')).toBeVisible();
    await expect(page.locator('input[name="url"]')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Description' })).toBeVisible();
    await expect(page.getByPlaceholder('Add more...')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Reset form' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Post' })).toBeVisible();

    // filling out event creation form
    await page.getByPlaceholder('Your Event Name Here...').click();
    await page.getByPlaceholder('Your Event Name Here...').fill('CS32 Party');
    await page.getByPlaceholder('Your Event Description Here...').click();
    await page.getByPlaceholder('Your Event Description Here...').fill('Fun fun fun!');
    await page.getByPlaceholder('Add more...').click();
    await page.getByPlaceholder('Add more...').fill('CS32');
    await page.getByPlaceholder('Add more...').press('Enter');
    await page.getByPlaceholder('Add more...').fill('Technology');
    await page.getByPlaceholder('Add more...').press('Enter');
    await page.getByPlaceholder('Add more...').fill('Fun');
    await page.getByPlaceholder('Add more...').press('Enter');
    await page.getByRole('button', { name: 'Post' }).click();

    // searching for event
    await page.getByPlaceholder('Search events...').click();
    await page.getByPlaceholder('Search events...').fill('CS32');
    await expect(page.getByText('CS32 PartyDate: Time: - Fun')).toBeVisible();
    await page.getByText('CS32 PartyDate: Time: - Fun').click();
    await expect(page.locator('h1')).toBeVisible();
    await expect(page.getByText('Fun fun fun!').nth(1)).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Attendees ·' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Edit' })).toBeVisible();
    await expect(page.getByText('CS32', { exact: true })).toBeVisible();
    await expect(page.getByText('Technology')).toBeVisible();
    await expect(page.getByText('Fun', { exact: true })).toBeVisible();
    
    // event editing
    await page.getByRole('button', { name: 'Edit' }).click();
    await page.getByRole('button', { name: 'Fun' }).click();
    await page.getByPlaceholder('Your Event Description Here...').click();
    await page.getByPlaceholder('Your Event Description Here...').fill('');
    await page.getByRole('button', { name: 'Save Changes' }).click();
    await page.waitForTimeout(3000);

    // checking edits show up
    await expect(page.getByText('Technology')).toBeVisible();
    await expect(page.getByText('Fun', { exact: true })).not.toBeVisible();
    await expect(page.getByText('Fun fun fun!').nth(1)).not.toBeVisible();

    // event deletion
    await page.getByRole('button', { name: 'Edit' }).click();
    page.once('dialog', dialog => {
        console.log(`Dialog message: ${dialog.message()}`);
        dialog.accept();
      });
    await page.getByRole('button', { name: 'Delete Event' }).click();

    await page.waitForTimeout(3000);

    // searching for event again
    await page.getByPlaceholder('Search events...').click();
    await page.getByPlaceholder('Search events...').fill('');
    await page.getByPlaceholder('Search events...').fill('CS32');
    await page.waitForTimeout(3000);
    await expect(page.getByRole('heading', { name: 'No Events Found' })).toBeVisible();
});

/**
 * Tests that searchbar functionality works as expected
 */

test('test search', async ({ page }) => {
    // checking latte art event shows up for "coffee"
    await page.getByRole('textbox', { name: 'Search events...' }).click();
    await page.getByRole('textbox', { name: 'Search events...' }).fill('Coffee');
    await page.waitForTimeout(3000);
    await expect(page.getByText('Latte Art 101Date: 2025-06-16')).toBeVisible();
    await page.getByRole('textbox', { name: 'Search events...' }).click();

    // checking game related events show up for "Game"
    await page.getByRole('textbox', { name: 'Search events...' }).fill('');
    await page.getByRole('textbox', { name: 'Search events...' }).fill('Game');
    await page.waitForTimeout(3000);
    await expect(page.getByText('Board Game BonanzaDate: 2025-')).toBeVisible();
    await expect(page.getByText('Ice Cream SocialDate: 2025-06')).toBeVisible();
    await expect(page.getByText('Trivia TournamentDate: 2025-')).toBeVisible();

    // check previous results (non relevant) don't appear
    await expect(page.getByText('Latte Art 101Date: 2025-06-16')).not.toBeVisible();

  });

/**
 * Tests that own profile page can be edited
 */

test('test profile edits', async ({ page }) => {
    await page.getByRole('button', { name: 'Profile' }).nth(1).click();
    await page.waitForTimeout(3000);

    // adding tags
    await page.locator('#add-tag').click();
    await page.locator('#add-tag').fill('Reading');
    await page.locator('#add-tag').press('Enter');
    await page.waitForTimeout(1000);
    await expect(page.getByRole('button', { name: 'Reading' })).toBeVisible();

    // removing tags and checking they no longer exist
    await page.getByRole('button', { name: 'Reading' }).click();
    await page.waitForTimeout(1000);
    await expect(page.getByRole('button', { name: 'Reading' })).not.toBeVisible();

})

/**
 * Tests that other's profile page CANNOT be edited
 */

test('test other profile views', async ({ page }) => {
    await page.getByRole('button', { name: 'Profile' }).nth(1).click();
    await page.waitForTimeout(3000);

    // clicking on event in event history

    await expect(page.getByText('Trail HikeA refreshing group')).toBeVisible();
    await page.getByText('Trail HikeA refreshing group').click();
    await page.waitForTimeout(3000);

    // checking attendence in RSVP block
    await expect(page.getByRole('button', { name: 'Aarav Kumar' })).toBeVisible();

    // clciking on other profile
    await page.getByRole('button', { name: 'Outdoor Adventures' }).click();
    await page.waitForTimeout(2000);
    await expect(page.getByRole('heading', { name: 'EvilGuy321' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'My Interests' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'My Favorite CanGo Organizers' })).toBeVisible();
    await expect(page.locator('#add-tag')).not.toBeVisible(); // should NOT be visible for other profile
    await expect(page.locator('#add-org')).not.toBeVisible(); // should NOT be visible for other profile

})