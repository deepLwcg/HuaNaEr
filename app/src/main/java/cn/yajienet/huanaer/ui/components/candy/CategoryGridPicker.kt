package cn.yajienet.huanaer.ui.components.candy

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.yajienet.huanaer.data.model.Category
import cn.yajienet.huanaer.util.CategoryEmoji

/** 固定两排，每排 5 个，单页最多 10 个；超出左右滑动翻页 */
private const val GRID_ROWS = 2
private const val GRID_COLUMNS = 5
private const val GRID_PAGE_SIZE = GRID_ROWS * GRID_COLUMNS

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryGridPicker(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (categories.isEmpty()) return

    val pages = remember(categories) { categories.chunked(GRID_PAGE_SIZE) }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val rowHeight = if (compact) 52.dp else 60.dp
    val rowSpacing = if (compact) 6.dp else 8.dp
    val maxRowsOnAnyPage = remember(pages) {
        pages.maxOfOrNull { pageRowCount(it.size) } ?: 1
    }
    val pagerHeight = rowHeight * maxRowsOnAnyPage + rowSpacing * (maxRowsOnAnyPage - 1).coerceAtLeast(0)
    val columnSpacing = if (compact) 4.dp else 6.dp

    LaunchedEffect(selectedCategoryId, categories) {
        if (selectedCategoryId == null) return@LaunchedEffect
        val index = categories.indexOfFirst { it.id == selectedCategoryId }
        if (index >= 0) {
            val targetPage = index / GRID_PAGE_SIZE
            if (targetPage != pagerState.currentPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight)
        ) { pageIndex ->
            CategoryGridPage(
                categories = pages[pageIndex],
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                compact = compact,
                rowSpacing = rowSpacing,
                columnSpacing = columnSpacing
            )
        }

        if (pages.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(if (selected) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

private fun pageRowCount(itemCount: Int): Int {
    if (itemCount <= 0) return 0
    return ((itemCount + GRID_COLUMNS - 1) / GRID_COLUMNS).coerceAtMost(GRID_ROWS)
}

@Composable
private fun CategoryGridPage(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    compact: Boolean,
    rowSpacing: Dp,
    columnSpacing: Dp
) {
    val rows = remember(categories) { categories.chunked(GRID_COLUMNS) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
        rows.forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(columnSpacing)
            ) {
                repeat(GRID_COLUMNS) { colIndex ->
                    val category = rowCategories.getOrNull(colIndex)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (category != null) {
                            CategoryGridItem(
                                category = category,
                                selected = selectedCategoryId == category.id,
                                onClick = { onCategorySelected(category.id) },
                                compact = compact
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryGridItem(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean
) {
    val bgColor = remember(category.color) {
        try {
            Color(category.color.removePrefix("#").toLong(16) or 0xFF000000)
        } catch (_: Exception) {
            null
        }
    } ?: MaterialTheme.colorScheme.primaryContainer

    val normalSize = if (compact) 36.dp else 42.dp
    val selectedSize = if (compact) 40.dp else 48.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        EmojiCategoryChip(
            emoji = CategoryEmoji.resolve(category.icon, category.name),
            selected = selected,
            onClick = onClick,
            backgroundColor = bgColor.copy(alpha = if (selected) 0.55f else 0.28f),
            glowColor = bgColor,
            normalSize = normalSize,
            selectedSize = selectedSize,
            normalEmojiSize = if (compact) 16.sp else 18.sp,
            selectedEmojiSize = if (compact) 18.sp else 22.sp,
            selectedScale = 1.05f,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = if (compact) 10.sp else 11.sp
            ),
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
